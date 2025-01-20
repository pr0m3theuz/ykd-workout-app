package com.example.myapplication.data.database

import com.mongodb.MongoException
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import org.bson.BsonInt64
import org.bson.Document

class MongoDBConnect {}

suspend fun setupDatabaseConnection(
  databaseName: String = "AppDatabase",
  connectionEnvVariable: String = "MONGODB_URI"
): MongoDatabase? {
  val connectString = if (System.getenv(connectionEnvVariable) != null) {
    System.getenv(connectionEnvVariable)
  } else {
    // Not a good practice only for demo purpose.
//        "mongodb+srv://<username>:<password>@cluster0.sq3aiau.mongodb.net/?retryWrites=true&w=majority"
    "mongodb+srv://postgres.srphxakyzxfoelpkrplw:p5xgn5KUkaXJzxPzxP62cqsQjT6bYuLU@192.168.1.195:27017/ferretdb?authMechanism=PLAIN"

  }

  val client = MongoClient.create(connectionString = connectString)
  val database = client.getDatabase(databaseName = databaseName)

  return try {
    // Send a ping to confirm a successful connection
    val command = Document("ping", BsonInt64(1))
    database.runCommand(command)
    println("Pinged your deployment. You successfully connected to MongoDB!")
    database
  } catch (me: MongoException) {
    System.err.println(me)
    null
  }
}