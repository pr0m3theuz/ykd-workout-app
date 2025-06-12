@file:UseSerializers(
  //  MutableRealmIntKSerializer::class,
  //  RealmAnyKSerializer::class,
  //  RealmDictionaryKSerializer::class,
  //  RealmInstantKSerializer::class,
  //  RealmListKSerializer::class,
  //  RealmSetKSerializer::class,
  //  RealmUUIDKSerializer::class,
)
@file:OptIn(ExperimentalSerializationApi::class)

package com.example.myapplication.common.domain.model

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.myapplication.common.util.ObjectIdSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import org.bson.types.ObjectId
import java.util.UUID

@Serializable
data class Plate(
  @SerialName("_id") val id: String = UUID.randomUUID().toString(),
  val weight: Float,
  val unit: String = "lb",
  var count: Int?, // Nullable for "unlimited"
  val color: String // Storing color as hex string e.g., "#FF0000" for Red
) {
  val displayCount: String
    get() = count?.toString() ?: "∞"
}

@Serializable
data class Barbell(
  @SerialName("_id") val id: String = UUID.randomUUID().toString(),
  val weight: Float,
  val unit: String = "lb"
)

@Serializable
data class Collar(
  @SerialName("_id") val id: String = UUID.randomUUID().toString(),
  val weight: Float,
  val unit: String = "lb"
)

@Serializable
data class UserProfile(
  @SerialName("id") @Serializable(with = ObjectIdSerializer::class) var id: ObjectId,
  var name: String? = null,
  var username: String? = null,
  var email: String? = null,
  var password: String? = null,
  var defaultWeightUnit: String = MassUnit.POUNDS.abbreviation, // TODO have enum class??????
  var intensity: String = Intensity.RIR.abbreviation, // TODO have enum class??????
  var authToken: String? = null,
  var isLoggedIn: Boolean = false,
  val plates: List<Plate> = emptyList(),
  val barbells: List<Barbell> = emptyList(),
  val collars: List<Collar> = emptyList(),
  private val type: String = "User"
)

// Define DataStore keys
object UserProfileDataStoreKeys {
  val USER_ID = stringPreferencesKey("user_id")
  val USER_NAME = stringPreferencesKey("user_name")
  val USER_EMAIL = stringPreferencesKey("user_email")
  val USER_USERNAME = stringPreferencesKey("user_username")
  val USER_PASSWORD = stringPreferencesKey("user_password")
  val DEFAULT_WEIGHT_UNIT = stringPreferencesKey("default_weight_unit")
  val DEFAULT_INTENSITY = stringPreferencesKey("default_intensity")
  val AUTH_TOKEN = stringPreferencesKey("auth_token")
  val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
}

val Context.userProfileDataStore by preferencesDataStore(name = "user_prefs")

//object UserSerializer : Serializer<User> {
//  override val defaultValue: User = User.getDefaultInstance()
//
//  override suspend fun readFrom(input: InputStream): User {
//    try {
//      return User.parseFrom(input)
//    } catch (exception: InvalidProtocolBufferException) {
//      throw CorruptionException("Cannot read proto.", exception)
//    }
//  }
//
//  override suspend fun writeTo(
//    t: User,
//    output: OutputStream
//  ) = t.writeTo(output)
//}
//
//val Context.userDataStore: DataStore<User> by dataStore(
//  fileName = "user.pb",
//  serializer = UserSerializer()
//)