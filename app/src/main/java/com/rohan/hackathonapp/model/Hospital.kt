package com.rohan.hackathonapp.model

data class Hospital(
    val state:String,
    val name:String,
    val admissionCapacity:String,
    val hospitalBeds:String,
    val distance:Double,
    val lat:Double,
    val long:Double
)