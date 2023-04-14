package com.livingtechusa.reflexion.data.models

data class ReflexionItem(
    val autogenPk: Int,
    val description: String,
    val detailedDescription: String,
    val name: String,
    val parent: Any,
    val videoUrl: String
)