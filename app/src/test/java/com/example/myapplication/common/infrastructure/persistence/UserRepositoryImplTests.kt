package com.example.myapplication.common.infrastructure.persistence

import com.couchbase.lite.*
import com.example.myapplication.common.domain.model.MassUnit
import com.example.myapplication.common.domain.model.UserProfile
import com.example.myapplication.common.infrastructure.database.DatabaseManager
import io.mockk.*
import org.bson.types.ObjectId
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserRepositoryImplTests {

    private lateinit var mockDatabaseManager: DatabaseManager
    private lateinit var mockDatabase: Database
    private lateinit var mockCollection: Collection
    private lateinit var userRepository: UserRepositoryImpl

    private val testUserId = ObjectId()
    private val testUserJson = """
    {
        "id": "${testUserId.toHexString()}",
        "name": "Test User",
        "username": "testuser",
        "email": "test@example.com",
        "password": "password",
        "defaultWeightUnit": "kg",
        "intensity": "RIR",
        "authToken": "test_token",
        "isLoggedIn": false,
        "type": "User"
    }
    """

    @Before
    fun setUp() {
        mockDatabaseManager = mockk()
        mockDatabase = mockk()
        mockCollection = mockk(relaxed = true)

        every { mockDatabaseManager.database } returns mockDatabase
        every { mockDatabase.getCollection("Users") } returns mockCollection

        every { mockCollection.save(any()) } just runs

        val mockDocument = mockk<MutableDocument>(relaxed = true)
        every { mockCollection.getDocument(any()) } returns mockDocument
        every { mockDocument.setString(any(), any<String>()) } returns mockDocument

        userRepository = UserRepositoryImpl(mockDatabaseManager)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `createUser stores user with MassUnit abbreviation`() {
        val userProfile = UserProfile(
            id = testUserId,
            name = "Test User",
            defaultWeightUnit = MassUnit.KILOGRAMS
        )
        val docSlot = slot<MutableDocument>()
        every { mockCollection.save(capture(docSlot)) } just runs

        userRepository.createUser(userProfile)

        verify { mockCollection.save(any()) }
        assertEquals(MassUnit.KILOGRAMS.abbreviation, docSlot.captured.getString("weightUnit"))
        assertEquals(userProfile.id.toHexString(), docSlot.captured.getString("id"))
    }

    @Test
    fun `updateCurrentUser stores user with MassUnit abbreviation`() {
        val userProfile = UserProfile(
            id = testUserId,
            name = "Test User Updated",
            defaultWeightUnit = MassUnit.POUNDS
        )

        val mockExistingDocument = mockk<MutableDocument>(relaxed = true)
        every { mockCollection.getDocument(userProfile.id.toHexString()) } returns mockExistingDocument

        val weightUnitSlot = slot<String>()
        every { mockExistingDocument.setString("weightUnit", capture(weightUnitSlot)) } returns mockExistingDocument

        userRepository.updateCurrentUser(userProfile)

        verify { mockCollection.getDocument(userProfile.id.toHexString()) }
        verify { mockExistingDocument.setString("weightUnit", MassUnit.POUNDS.abbreviation) }
        verify { mockCollection.save(mockExistingDocument) }

        assertEquals(MassUnit.POUNDS.abbreviation, weightUnitSlot.captured)
    }

    @Test
    fun `getUser retrieves user and correctly deserializes MassUnit`() {
        val userProfileToGet = UserProfile(id = testUserId)

        val mockQuery = mockk<Query>()
        val mockResultSet = mockk<ResultSet>()
        val mockResult = mockk<Result>()

        mockkStatic(QueryBuilder::class)
        val mockSelect = mockk<Select>()
        val mockFrom = mockk<From>()
        val mockWhere = mockk<Where>()
        val mockLimit = mockk<Limit>()

        every { QueryBuilder.select(any<SelectResult>()) } returns mockSelect
        every { mockSelect.from(any<DataSource>()) } returns mockFrom
        every { mockFrom.where(any()) } returns mockWhere
        every { mockWhere.limit(any()) } returns mockLimit
        every { mockLimit.execute() } returns mockResultSet

        every { mockResultSet.allResults() } returns listOf(mockResult)
        every { mockResult.toJSON() } returns testUserJson

        val retrievedUser = userRepository.getUser(userProfileToGet)

        assertNotNull(retrievedUser)
        assertEquals(testUserId, retrievedUser!!.id)
        assertEquals(MassUnit.KILOGRAMS, retrievedUser.defaultWeightUnit)
        assertEquals("Test User", retrievedUser.name)
    }

    @Test
    fun `getUser returns null when user not found`() {
        val userProfileToGet = UserProfile(id = ObjectId())

        val mockQuery = mockk<Query>()
        val mockResultSet = mockk<ResultSet>()

        mockkStatic(QueryBuilder::class)
        val mockSelect = mockk<Select>()
        val mockFrom = mockk<From>()
        val mockWhere = mockk<Where>()
        val mockLimit = mockk<Limit>()

        every { QueryBuilder.select(any<SelectResult>()) } returns mockSelect
        every { mockSelect.from(any<DataSource>()) } returns mockFrom
        every { mockFrom.where(any()) } returns mockWhere
        every { mockWhere.limit(any()) } returns mockLimit
        every { mockLimit.execute() } returns mockResultSet

        every { mockResultSet.allResults() } returns emptyList()

        val retrievedUser = userRepository.getUser(userProfileToGet)
        assertNull(retrievedUser)
    }
}
