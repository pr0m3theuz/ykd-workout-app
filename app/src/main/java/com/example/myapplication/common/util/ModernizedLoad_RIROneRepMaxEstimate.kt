package com.example.myapplication.common.util

import com.example.myapplication.common.domain.model.Exercise
import kotlin.math.max
import kotlin.math.min

// Enum to represent the determined rep capacity category.
// Enum to represent different rep capacities based on the provided charts.
enum class RepCapacity {
  AVERAGE, HIGH, LOW
}

// Data class to hold the relevant data parsed from the Setup CSV file.
// This data is assumed to be loaded and available from your database/storage.
data class RepCapacityLookupData(
  val loadPercent: Double, // Relative Load as a percentage (e.g., 80.0)
  val lowRepsRounded: Int,  // Rounded lower bound of the expected rep range
  val highRepsRounded: Int // Rounded upper bound of the expected rep range
)


// Data structure to hold the percentage of 1RM for different rep capacities.
// The structure is a Map where the key is the RepCapacity enum, and the value
// is another Map. This inner Map has the RIR (Reps in Reserve) as the key
// and a Map of Reps Performed to the corresponding Percentage of 1RM (as a Double).
val modernizedLoadData: Map<RepCapacity, Map<Int, Map<Int, Double>>> = mapOf(
  RepCapacity.AVERAGE to mapOf(
    // Data from "Modernized Load/RIR Chart - Average Rep Capacity.pdf"
    // RIR 0 (RPE 10)
    0 to mapOf(
      1 to 1.00, 2 to 0.9759, 3 to 0.9560, 4 to 0.9284, 5 to 0.8946,
      6 to 0.8679, 7 to 0.8409, 8 to 0.8203, 9 to 0.8002, 10 to 0.7807,
      11 to 0.7616, 12 to 0.7429, 13 to 0.7248, 14 to 0.7070, 15 to 0.6897,
      16 to 0.6729, 17 to 0.6564, 18 to 0.6404, 19 to 0.6247, 20 to 0.6094,
      21 to 0.5945, 22 to 0.5800, 23 to 0.5658, 24 to 0.5519, 25 to 0.5384,
      26 to 0.5253, 27 to 0.5124, 28 to 0.4999, 29 to 0.4877
    ),
// RIR 1 (RPE 9)
    1 to mapOf(
      1 to 0.9755, 2 to 0.9517, 3 to 0.9284, 4 to 0.9057, 5 to 0.8835,
      6 to 0.8619, 7 to 0.8409, 8 to 0.8203, 9 to 0.8002, 10 to 0.7807,
      11 to 0.7616, 12 to 0.7429, 13 to 0.7248, 14 to 0.7070, 15 to 0.6897,
      16 to 0.6729, 17 to 0.6564, 18 to 0.6404, 19 to 0.6247, 20 to 0.6094,
      21 to 0.5945, 22 to 0.5800, 23 to 0.5658, 24 to 0.5519, 25 to 0.5384,
      26 to 0.5253, 27 to 0.5124, 28 to 0.4999
    ),
// RIR 2 (RPE 8)
    2 to mapOf(
      1 to 0.9400, 2 to 0.9170, 3 to 0.8946, 4 to 0.8727, 5 to 0.8513,
      6 to 0.8305, 7 to 0.8102, 8 to 0.7904, 9 to 0.7710, 10 to 0.7522,
      11 to 0.7338, 12 to 0.7158, 13 to 0.6983, 14 to 0.6813, 15 to 0.6646,
      16 to 0.6483, 17 to 0.6325, 18 to 0.6170, 19 to 0.6019, 20 to 0.5872,
      21 to 0.5728, 22 to 0.5588, 23 to 0.5452, 24 to 0.5318, 25 to 0.5188,
      26 to 0.5061, 27 to 0.4937
    ),
// RIR 3 (RPE 7)
    3 to mapOf(
      1 to 0.9057, 2 to 0.8835, 3 to 0.8619, 4 to 0.8409, 5 to 0.8203,
      6 to 0.8002, 7 to 0.7807, 8 to 0.7616, 9 to 0.7429, 10 to 0.7248,
      11 to 0.7070, 12 to 0.6897, 13 to 0.6729, 14 to 0.6564, 15 to 0.6404,
      16 to 0.6247, 17 to 0.6094, 18 to 0.5945, 19 to 0.5800, 20 to 0.5658,
      21 to 0.5519, 22 to 0.5384, 23 to 0.5253, 24 to 0.5124, 25 to 0.4999,
      26 to 0.4877
    ),
// RIR 4 (RPE 6)
    4 to mapOf(
      1 to 0.8727, 2 to 0.8513, 3 to 0.8305, 4 to 0.8102, 5 to 0.7904,
      6 to 0.7710, 7 to 0.7522, 8 to 0.7338, 9 to 0.7158, 10 to 0.6983,
      11 to 0.6813, 12 to 0.6646, 13 to 0.6483, 14 to 0.6325, 15 to 0.6170,
      16 to 0.6019, 17 to 0.5872, 18 to 0.5728, 19 to 0.5588, 20 to 0.5452,
      21 to 0.5318, 22 to 0.5188, 23 to 0.5061, 24 to 0.4937
    ),
// RIR 5 (RPE 5)
    5 to mapOf(
      1 to 0.8409, 2 to 0.8203, 3 to 0.8002, 4 to 0.7807, 5 to 0.7616,
      6 to 0.7429, 7 to 0.7248, 8 to 0.7070, 9 to 0.6897, 10 to 0.6729,
      11 to 0.6564, 12 to 0.6404, 13 to 0.6247, 14 to 0.6094, 15 to 0.5945,
      16 to 0.5800, 17 to 0.5658, 18 to 0.5519, 19 to 0.5384, 20 to 0.5253,
      21 to 0.5124, 22 to 0.4999, 23 to 0.4877
    ),
// RIR 6 (RPE 4)
    6 to mapOf(
      1 to 0.8102, 2 to 0.7904, 3 to 0.7710, 4 to 0.7522, 5 to 0.7338,
      6 to 0.7158, 7 to 0.6983, 8 to 0.6813, 9 to 0.6646, 10 to 0.6483,
      11 to 0.6325, 12 to 0.6170, 13 to 0.6019, 14 to 0.5872, 15 to 0.5728,
      16 to 0.5588, 17 to 0.5452, 18 to 0.5318, 19 to 0.5188, 20 to 0.5061,
      21 to 0.4937
    ),
// RIR 7 (RPE 3)
    7 to mapOf(
      1 to 0.7807, 2 to 0.7616, 3 to 0.7429, 4 to 0.7248, 5 to 0.7070,
      6 to 0.6897, 7 to 0.6729, 8 to 0.6564, 9 to 0.6404, 10 to 0.6247,
      11 to 0.6094, 12 to 0.5945, 13 to 0.5800, 14 to 0.5658, 15 to 0.5519,
      16 to 0.5384, 17 to 0.5253, 18 to 0.5124, 19 to 0.4999, 20 to 0.4877
    ),
// RIR 8 (RPE 2)
    8 to mapOf(
      1 to 0.7522, 2 to 0.7338, 3 to 0.7158, 4 to 0.6983, 5 to 0.6813,
      6 to 0.6646, 7 to 0.6483, 8 to 0.6325, 9 to 0.6170, 10 to 0.6019,
      11 to 0.5872, 12 to 0.5728, 13 to 0.5588, 14 to 0.5452, 15 to 0.5318,
      16 to 0.5188, 17 to 0.5061, 18 to 0.4937
    ),
// RIR 9 (RPE 1)
    9 to mapOf(
      1 to 0.7248, 2 to 0.7070, 3 to 0.6897, 4 to 0.6729, 5 to 0.6564,
      6 to 0.6404, 7 to 0.6247, 8 to 0.6094, 9 to 0.5945, 10 to 0.5800,
      11 to 0.5658, 12 to 0.5519, 13 to 0.5384, 14 to 0.5253, 15 to 0.5124,
      16 to 0.4999, 17 to 0.4877
    ),
// RIR 10 (RPE 0)
    10 to mapOf(
      1 to 0.6983, 2 to 0.6813, 3 to 0.6646, 4 to 0.6483, 5 to 0.6325,
      6 to 0.6170, 7 to 0.6019, 8 to 0.5872, 9 to 0.5728, 10 to 0.5588,
      11 to 0.5452, 12 to 0.5318, 13 to 0.5188, 14 to 0.5061, 15 to 0.4937
    )
  ),
  RepCapacity.HIGH to mapOf(
// Data from "Modernized Load/RIR Chart - High Rep Capacity.pdf"
// RIR 0 (RPE 10)
    0 to mapOf(
      1 to 1.00, 2 to 0.9623, 3 to 0.9441, 4 to 0.9263, 5 to 0.9088,
      6 to 0.8917, 7 to 0.8750, 8 to 0.8585, 9 to 0.8424, 10 to 0.8266,
      11 to 0.8111, 12 to 0.7959, 13 to 0.7810, 14 to 0.7664, 15 to 0.7520,
      16 to 0.7379, 17 to 0.7241, 18 to 0.7105, 19 to 0.6972, 20 to 0.6841,
      21 to 0.6732, 22 to 0.6585, 23 to 0.6461, 24 to 0.6339, 25 to 0.6219,
      26 to 0.6101, 27 to 0.5986, 28 to 0.5872, 29 to 0.5760
    ),
// RIR 1 (RPE 9)
    1 to mapOf(
      1 to 0.9716, 2 to 0.9532, 3 to 0.9352, 4 to 0.9175, 5 to 0.9002,
      6 to 0.8833, 7 to 0.8667, 8 to 0.8504, 9 to 0.8345, 10 to 0.8188,
      11 to 0.8035, 12 to 0.7884, 13 to 0.7737, 14 to 0.7592, 15 to 0.7449,
      16 to 0.7310, 17 to 0.7173, 18 to 0.7038, 19 to 0.6906, 20 to 0.6776,
      21 to 0.6648, 22 to 0.6523, 23 to 0.6400, 24 to 0.6279, 25 to 0.6160,
      26 to 0.6043, 27 to 0.5929, 28 to 0.5816, 29 to 0.5705
    ),
// RIR 2 (RPE 8)
    2 to mapOf(
      1 to 0.9441, 2 to 0.9263, 3 to 0.9088, 4 to 0.8917, 5 to 0.8750,
      6 to 0.8585, 7 to 0.8424, 8 to 0.8266, 9 to 0.8111, 10 to 0.7959,
      11 to 0.7810, 12 to 0.7664, 13 to 0.7520, 14 to 0.7379, 15 to 0.7241,
      16 to 0.7105, 17 to 0.6972, 18 to 0.6841, 19 to 0.6712, 20 to 0.6585,
      21 to 0.6461, 22 to 0.6339, 23 to 0.6219, 24 to 0.6101, 25 to 0.5986,
      26 to 0.5872, 27 to 0.5760
    ),
// RIR 3 (RPE 7)
    3 to mapOf(
      1 to 0.9088, 2 to 0.8917, 3 to 0.8750, 4 to 0.8585, 5 to 0.8424,
      6 to 0.8266, 7 to 0.8111, 8 to 0.7959, 9 to 0.7810, 10 to 0.7664,
      11 to 0.7520, 12 to 0.7379, 13 to 0.7241, 14 to 0.7105, 15 to 0.6972,
      16 to 0.6841, 17 to 0.6712, 18 to 0.6585, 19 to 0.6461, 20 to 0.6339,
      21 to 0.6219, 22 to 0.6101, 23 to 0.5986, 24 to 0.5872, 25 to 0.5760
    ),
// RIR 4 (RPE 6)
    4 to mapOf(
      1 to 0.8750, 2 to 0.8585, 3 to 0.8424, 4 to 0.8266, 5 to 0.8111,
      6 to 0.7959, 7 to 0.7810, 8 to 0.7664, 9 to 0.7520, 10 to 0.7379,
      11 to 0.7241, 12 to 0.7105, 13 to 0.6972, 14 to 0.6841, 15 to 0.6712,
      16 to 0.6585, 17 to 0.6461, 18 to 0.6339, 19 to 0.6219, 20 to 0.6101,
      21 to 0.5986, 22 to 0.5872, 23 to 0.5760
    ),
// RIR 5 (RPE 5)
    5 to mapOf(
      1 to 0.8424, 2 to 0.8266, 3 to 0.8111, 4 to 0.7959, 5 to 0.7810,
      6 to 0.7664, 7 to 0.7520, 8 to 0.7379, 9 to 0.7241, 10 to 0.7105,
      11 to 0.6972, 12 to 0.6841, 13 to 0.6712, 14 to 0.6585, 15 to 0.6461,
      16 to 0.6339, 17 to 0.6219, 18 to 0.6101, 19 to 0.5986, 20 to 0.5872,
      21 to 0.5760
    ),
// RIR 6 (RPE 4)
    6 to mapOf(
      1 to 0.8111, 2 to 0.7959, 3 to 0.7810, 4 to 0.7664, 5 to 0.7520,
      6 to 0.7379, 7 to 0.7241, 8 to 0.7105, 9 to 0.6972, 10 to 0.6841,
      11 to 0.6712, 12 to 0.6585, 13 to 0.6461, 14 to 0.6339, 15 to 0.6219,
      16 to 0.6101, 17 to 0.5986, 18 to 0.5872, 19 to 0.5760
    ),
// RIR 7 (RPE 3)
    7 to mapOf(
      1 to 0.7810, 2 to 0.7664, 3 to 0.7520, 4 to 0.7379, 5 to 0.7241,
      6 to 0.7105, 7 to 0.6972, 8 to 0.6841, 9 to 0.6712, 10 to 0.6585,
      11 to 0.6461, 12 to 0.6339, 13 to 0.6219, 14 to 0.6101, 15 to 0.5986,
      16 to 0.5872, 17 to 0.5760
    ),
// RIR 8 (RPE 2)
    8 to mapOf(
      1 to 0.7520, 2 to 0.7379, 3 to 0.7241, 4 to 0.7105, 5 to 0.6972,
      6 to 0.6841, 7 to 0.6712, 8 to 0.6585, 9 to 0.6461, 10 to 0.6339,
      11 to 0.6219, 12 to 0.6101, 13 to 0.5986, 14 to 0.5872, 15 to 0.5760
    ),
// RIR 9 (RPE 1)
    9 to mapOf(
      1 to 0.7241, 2 to 0.7105, 3 to 0.6972, 4 to 0.6841, 5 to 0.6712,
      6 to 0.6585, 7 to 0.6461, 8 to 0.6339, 9 to 0.6219, 10 to 0.6101,
      11 to 0.5986, 12 to 0.5872, 13 to 0.5760
    ),
// RIR 10 (RPE 0)
    10 to mapOf(
      1 to 0.6972, 2 to 0.6841, 3 to 0.6712, 4 to 0.6585, 5 to 0.6461,
      6 to 0.6339, 7 to 0.6219, 8 to 0.6101, 9 to 0.5986, 10 to 0.5872,
      11 to 0.5760
    )
  ),
  RepCapacity.LOW to mapOf(
// Data from "Modernized Load/RIR Chart - Low Rep Capacity.pdf"
// RIR 0 (RPE 10)
    0 to mapOf(
      1 to 1.00, 2 to 0.9697, 3 to 0.9323, 4 to 0.8999, 5 to 0.8600,
      6 to 0.8379, 7 to 0.8084, 8 to 0.7800, 9 to 0.7525, 10 to 0.7260,
      11 to 0.7006, 12 to 0.6760, 13 to 0.6525, 14 to 0.6298, 15 to 0.6081,
      16 to 0.5872, 17 to 0.5671, 18 to 0.5479, 19 to 0.5294, 20 to 0.5116,
      21 to 0.4946, 22 to 0.4782, 23 to 0.4625, 24 to 0.4474, 25 to 0.4329,
      26 to 0.4189, 27 to 0.4055, 28 to 0.3926, 29 to 0.3802, 30 to 0.3682
    ),
// RIR 1 (RPE 9)
    1 to mapOf(
      1 to 0.9820, 2 to 0.9657, 3 to 0.9323, 4 to 0.9150, 5 to 0.8999,
      6 to 0.8640, 7 to 0.8530, 8 to 0.8379, 9 to 0.8084, 10 to 0.7941,
      11 to 0.7800, 12 to 0.7661, 13 to 0.7525, 14 to 0.7391, 15 to 0.7260,
      16 to 0.7132, 17 to 0.7006, 18 to 0.6882, 19 to 0.6760, 20 to 0.6641,
      21 to 0.6525, 22 to 0.6411, 23 to 0.6298, 24 to 0.6189, 25 to 0.6081,
      26 to 0.5975, 27 to 0.5872, 28 to 0.5771, 29 to 0.5671, 30 to 0.5574
    ),
// RIR 2 (RPE 8)
    2 to mapOf(
      1 to 0.9516, 2 to 0.9238, 3 to 0.8999, 4 to 0.8684, 5 to 0.8530,
      6 to 0.8379, 7 to 0.8230, 8 to 0.8084, 9 to 0.7941, 10 to 0.7800,
      11 to 0.7661, 12 to 0.7525, 13 to 0.7391, 14 to 0.7260, 15 to 0.7132,
      16 to 0.7006, 17 to 0.6882, 18 to 0.6760, 19 to 0.6641, 20 to 0.6525,
      21 to 0.6411, 22 to 0.6298, 23 to 0.6189, 24 to 0.6081, 25 to 0.5975,
      26 to 0.5872, 27 to 0.5771, 28 to 0.5671
    ),
// RIR 3 (RPE 7)
    3 to mapOf(
      1 to 0.9323, 2 to 0.8999, 3 to 0.8684, 4 to 0.8530, 5 to 0.8230,
      6 to 0.8084, 7 to 0.7941, 8 to 0.7800, 9 to 0.7661, 10 to 0.7525,
      11 to 0.7391, 12 to 0.7260, 13 to 0.7132, 14 to 0.7006, 15 to 0.6882,
      16 to 0.6760, 17 to 0.6641, 18 to 0.6525, 19 to 0.6411, 20 to 0.6298,
      21 to 0.6189, 22 to 0.6081, 23 to 0.5975, 24 to 0.5872, 25 to 0.5771,
      26 to 0.5671
    ),
// RIR 4 (RPE 6)
    4 to mapOf(
      1 to 0.8999, 2 to 0.8684, 3 to 0.8530, 4 to 0.8230, 5 to 0.8084,
      6 to 0.7941, 7 to 0.7800, 8 to 0.7661, 9 to 0.7525, 10 to 0.7391,
      11 to 0.7260, 12 to 0.7132, 13 to 0.7006, 14 to 0.6882, 15 to 0.6760,
      16 to 0.6641, 17 to 0.6525, 18 to 0.6411, 19 to 0.6298, 20 to 0.6189,
      21 to 0.6081, 22 to 0.5975, 23 to 0.5872, 24 to 0.5771
    ),
// RIR 5 (RPE 5)
    5 to mapOf(
      1 to 0.8684, 2 to 0.8530, 3 to 0.8230, 4 to 0.8084, 5 to 0.7941,
      6 to 0.7800, 7 to 0.7661, 8 to 0.7525, 9 to 0.7391, 10 to 0.7260,
      11 to 0.7132, 12 to 0.7006, 13 to 0.6882, 14 to 0.6760, 15 to 0.6641,
      16 to 0.6525, 17 to 0.6411, 18 to 0.6298, 19 to 0.6189, 20 to 0.6081,
      21 to 0.5975, 22 to 0.5872, 23 to 0.5771
    ),
// RIR 6 (RPE 4)
    6 to mapOf(
      1 to 0.8379, 2 to 0.8230, 3 to 0.8084, 4 to 0.7941, 5 to 0.7800,
      6 to 0.7661, 7 to 0.7525, 8 to 0.7391, 9 to 0.7260, 10 to 0.7132,
      11 to 0.7006, 12 to 0.6882, 13 to 0.6760, 14 to 0.6641, 15 to 0.6525,
      16 to 0.6411, 17 to 0.6298, 18 to 0.6189, 19 to 0.6081, 20 to 0.5975,
      21 to 0.5872
    ),
// RIR 7 (RPE 3)
    7 to mapOf(
      1 to 0.8084, 2 to 0.7941, 3 to 0.7800, 4 to 0.7661, 5 to 0.7525,
      6 to 0.7391, 7 to 0.7260, 8 to 0.7132, 9 to 0.7006, 10 to 0.6882,
      11 to 0.6760, 12 to 0.6641, 13 to 0.6525, 14 to 0.6411, 15 to 0.6298,
      16 to 0.6189, 17 to 0.6081, 18 to 0.5975, 19 to 0.5872
    ),
// RIR 8 (RPE 2)
    8 to mapOf(
      1 to 0.7800, 2 to 0.7661, 3 to 0.7525, 4 to 0.7391, 5 to 0.7260,
      6 to 0.7132, 7 to 0.7006, 8 to 0.6882, 9 to 0.6760, 10 to 0.6641,
      11 to 0.6525, 12 to 0.6411, 13 to 0.6298, 14 to 0.6189, 15 to 0.6081,
      16 to 0.5975, 17 to 0.5872
    ),
// RIR 9 (RPE 1)
    9 to mapOf(
      1 to 0.7525, 2 to 0.7391, 3 to 0.7260, 4 to 0.7132, 5 to 0.7006,
      6 to 0.6882, 7 to 0.6760, 8 to 0.6641, 9 to 0.6525, 10 to 0.6411,
      11 to 0.6298, 12 to 0.6189, 13 to 0.6081, 14 to 0.5975, 15 to 0.5872
    ),
// RIR 10 (RPE 0)
    10 to mapOf(
      1 to 0.7260, 2 to 0.7132, 3 to 0.7006, 4 to 0.6882, 5 to 0.6760,
      6 to 0.6641, 7 to 0.6525, 8 to 0.6411, 9 to 0.6298, 10 to 0.6189,
      11 to 0.6081, 12 to 0.5975, 13 to 0.5872
    )
  )
)

/**
 * Estimates the 1 Rep Max (1RM) based on weight lifted, repetitions, RIR, and rep capacity.
 *
 * This function uses data from the "Modernized Load/RIR Chart" from Data Driven Strength
 * and Nuzzo, et al. (2023) to find the estimated percentage of 1RM for the given reps and RIR,
 * and then calculates the 1RM.
 * It includes linear interpolation for reps and RIR values that fall between the chart data points.
 *
 * @param weight The weight lifted (e.g., in kg or lbs).
 * @param reps The number of repetitions performed.
 * @param rir The Reps in Reserve (0 for failure, 1 for 1 rep left, etc.).
 * @param repCapacity The user's rep capacity type (AVERAGE, HIGH, or LOW). Defaults to AVERAGE.
 * @return The estimated 1RM as a Double, or null if the estimation is not possible
 * (e.g., invalid input or chart data not found).
 */
fun estimateOneRepMaxModernized(
  weight: Double,
  reps: Int,
  rir: Int,
  repCapacity: RepCapacity = RepCapacity.AVERAGE
): Double? {
  // Get the appropriate chart data based on the specified rep capacity
  val selectedChartData = modernizedLoadData[repCapacity]
    ?: return null // Return null if the chart data for the specified capacity is not found

  // Get the percentage of 1RM from the selected chart with interpolation
  val percentage = getPercentageFromChart(rir, reps, selectedChartData)

  // Calculate estimated 1RM if percentage is found and is greater than 0
  return if (percentage != null && percentage > 0) {
    weight / percentage
  } else {
    null // Unable to estimate 1RM
  }
}

/**
 * Helper function to convert RPE (Rating of Perceived Exertion) to RIR (Reps in Reserve).
 *
 * The RPE scale used in the provided charts is typically 10 - RIR.
 *
 * @param rpe The RPE value (typically from 0 to 10).
 * @return The corresponding RIR value. Returns 0 for RPE >= 10, and 10 for RPE <= 0.
 */
fun rpeToRir(rpe: Int): Int {
  return max(0, min(10, 10 - rpe)) // Ensure RIR is within a reasonable range [0, 10]
}

/**
 * Gets the percentage of 1RM from the chart for a given RIR and number of reps,
 * with linear interpolation for values between chart data points.
 *
 * @param rir The Reps in Reserve.
 * @param reps The number of repetitions performed.
 * @param chartData The specific chart data (e.g., averageRepCapacityChart).
 * @return The estimated percentage of 1RM as a Double, or null if the value
 * cannot be determined from the chart.
 */
private fun getPercentageFromChart(
  rir: Int,
  reps: Int,
  chartData: Map<Int, Map<Int, Double>>
): Double? {
  val rirEntries = chartData.entries.sortedBy { it.key }

  // Find the two closest RIR values in the chart data
  val lowerRirEntry = rirEntries.findLast { it.key <= rir }
  val upperRirEntry = rirEntries.find { it.key >= rir }

  when {
    lowerRirEntry != null && upperRirEntry != null -> {
      val lowerRir = lowerRirEntry.key
      val upperRir = upperRirEntry.key
      val lowerRepsMap = lowerRirEntry.value
      val upperRepsMap = upperRirEntry.value

      // Get percentage for the given reps at the lower and upper RIRs
      val lowerPercentage = getPercentageForReps(reps, lowerRepsMap)
      val upperPercentage = getPercentageForReps(reps, upperRepsMap)

      return when {
        lowerPercentage != null && upperPercentage != null -> {
          if (lowerRir == upperRir) {
            // Exact RIR match, return the percentage found for the reps
            lowerPercentage
          } else {
            // Interpolate linearly between the percentages at the lower and upper RIRs
            // Formula: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
            // x = rir, y = percentage, x1 = lowerRir, y1 = lowerPercentage, x2 = upperRir, y2 = upperPercentage
            lowerPercentage + (rir - lowerRir).toDouble() * (upperPercentage - lowerPercentage) / (upperRir - lowerRir)
          }
        }

        lowerPercentage != null -> lowerPercentage // Use lower RIR percentage if upper is null
        upperPercentage != null -> upperPercentage // Use upper RIR percentage if lower is null
        else -> null // Cannot find percentage for the given reps at either RIR
      }
    }

    lowerRirEntry != null -> {
      // RIR is higher than the max RIR in the chart, use the percentage for the max RIR
      return getPercentageForReps(reps, lowerRirEntry.value)
    }

    upperRirEntry != null -> {
      // RIR is lower than the min RIR in the chart, use the percentage for the min RIR
      return getPercentageForReps(reps, upperRirEntry.value)
    }

    else -> return null // Chart data is empty or invalid
  }
}

/**
 * Helper function to get the percentage of 1RM for a given number of reps
 * within a specific RIR's data, with linear interpolation for values between
 * chart data points.
 *
 * @param reps The number of repetitions performed.
 * @param repsMap The map of reps to percentages for a specific RIR.
 * @return The estimated percentage of 1RM as a Double, or null if the value
 * cannot be determined from the reps map.
 */
private fun getPercentageForReps(reps: Int, repsMap: Map<Int, Double>): Double? {
  val repEntries = repsMap.entries.sortedBy { it.key }

  // Find the two closest rep values in the map
  val lowerRepEntry = repEntries.findLast { it.key <= reps }
  val upperRepEntry = repEntries.find { it.key >= reps }

  return when {
    lowerRepEntry != null && upperRepEntry != null -> {
      val lowerRep = lowerRepEntry.key
      val upperRep = upperRepEntry.key
      val lowerPercentage = lowerRepEntry.value
      val upperPercentage = upperRepEntry.value

      if (lowerRep == upperRep) {
        // Exact reps match, return the percentage
        lowerPercentage
      } else {
        // Interpolate linearly between the percentages for the lower and upper reps
        // Formula: y = y1 + (x - x1) * (y2 - y1) / (x2 - x1)
        // x = reps, y = percentage, x1 = lowerRep, y1 = lowerPercentage, x2 = upperRep, y2 = upperPercentage
        lowerPercentage + (reps - lowerRep).toDouble() * (upperPercentage - lowerPercentage) / (upperRep - lowerRep)
      }
    }

    lowerRepEntry != null -> lowerRepEntry.value // Reps is higher than max reps in map, use max reps percentage
    upperRepEntry != null -> upperRepEntry.value // Reps is lower than min reps in map, use min reps percentage
    else -> null // Reps map is empty or invalid
  }
}

/**
 * Determines the rep capacity (Low, Average, High) based on the relative load and actual reps performed to failure.
 *
 * Uses data from the parsed Setup CSV table to find the expected rep range for a given
 * relative load and compares the actual reps performed to this range.
 * This function assumes the `repCapacityLookupTable` is already loaded and sorted by loadPercent.
 *
 * @param relativeLoadPercent The percentage of 1RM lifted (e.g., 80.0 for 80%). Must be between 0 and 100.
 * @param actualRepsPerformed The actual number of repetitions performed to failure. Must be non-negative.
 * @param repCapacityLookupTable The parsed data from the Setup CSV (List<RepCapacityLookupData>), assumed to be sorted by loadPercent.
 * @return The RepCapacityCategory (LOW_CAPACITY, AVERAGE_CAPACITY, HIGH_CAPACITY),
 * or null if the input is invalid or lookup fails (e.g., empty lookup table, load outside effective table range).
 */
fun determineRepCapacity(
  relativeLoadPercent: Double,
  actualRepsPerformed: Int,
  repCapacityLookupTable: List<RepCapacityLookupData>
): RepCapacity? {
  // Validate inputs
  if (relativeLoadPercent < 15 || relativeLoadPercent > 100 || actualRepsPerformed < 0 || repCapacityLookupTable.isEmpty()) {
    println("Error: Invalid input for rep capacity determination.")
    return null // Invalid input or empty lookup table
  }

  // Find the entry in the table with the largest loadPercent less than or equal to the relativeLoadPercent.
  // This mimics the approximate match behavior of VLOOKUP(..., TRUE).
  // Since the table is sorted, we can use findLast.
  val relevantEntry = repCapacityLookupTable.findLast { it.loadPercent <= relativeLoadPercent }

  // If no entry is found (shouldn't happen if loadPercent is >= min table load), or if the load is
  // less than the minimum in the table, use the first entry's reps as the range.
  // If the load is greater than the maximum in the table, use the last entry's reps.
  val lookupEntry = when {
    relevantEntry == null -> {
      println("Warning: Relative load ($relativeLoadPercent%) is below the minimum table load. Using data for minimum load.")
      repCapacityLookupTable.first() // Use the first entry's range
    }

    relativeLoadPercent > repCapacityLookupTable.last().loadPercent -> {
      println("Warning: Relative load ($relativeLoadPercent%) is above the maximum table load. Using data for maximum load.")
      repCapacityLookupTable.last() // Use the last entry's range
    }

    else -> relevantEntry // Found a relevant entry <= relativeLoadPercent
  }

  // Get the expected rounded rep range from the found entry
  val expectedLowReps = lookupEntry.lowRepsRounded
  val expectedHighReps = lookupEntry.highRepsRounded

  // Determine rep capacity based on actual reps performed and the expected range
  return when {
    actualRepsPerformed < expectedLowReps -> RepCapacity.LOW
    actualRepsPerformed > expectedHighReps -> RepCapacity.HIGH
    else -> RepCapacity.AVERAGE
  }
}

/**
 * Performs a simulated 1RM test based on an estimated 1RM and a test set to determine
 * and update the exercise's rep capacity.
 *
 * This function calculates the relative load of the test set and uses the rep capacity
 * lookup table to determine the exercise's rep capacity category. It then updates
 * the provided Exercise object.
 *
 * @param exercise The Exercise object to update.
 * @param estimated1RM The user's estimated 1RM for this exercise. Must be greater than 0.
 * @param testWeight The weight lifted during the test set to failure.
 * @param testRepsToFailure The number of repetitions performed to failure during the test set.
 * @param repCapacityLookupTable The parsed data from the Setup CSV (List<RepCapacityLookupData>), assumed to be sorted by loadPercent.
 * @return true if the rep capacity was successfully determined and updated, false otherwise.
 */
fun performRepCapacityTest(
  exercise: Exercise,
  estimated1RM: Double,
  testWeight: Double,
  testRepsToFailure: Int,
  repCapacityLookupTable: List<RepCapacityLookupData>
): Boolean {
  // Validate estimated 1RM to avoid division by zero
  if (estimated1RM <= 0) {
    println("Error: Estimated 1RM must be greater than 0 to perform a rep capacity test.")
    return false
  }

  // Calculate the relative load percentage of the test set
  val relativeLoadPercent = (testWeight / estimated1RM) * 100.0

  // Determine the rep capacity category
  val determinedCapacity = determineRepCapacity(
    relativeLoadPercent = relativeLoadPercent,
    actualRepsPerformed = testRepsToFailure,
    repCapacityLookupTable = repCapacityLookupTable
  )

  // Update the exercise's rep capacity if determined
  return if (determinedCapacity != null) {
    exercise.repCapacity = determinedCapacity
    println("Rep capacity for ${exercise.name} updated to ${exercise.repCapacity}.")
    // In a real application, you would save the updated exercise object to your database here.
    // saveExerciseToDatabase(exercise) // Example database call
    true
  } else {
    println("Failed to determine rep capacity for ${exercise.name}. Exercise data not updated.")
    false
  }
}
