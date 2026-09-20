package com.example.data

import com.example.model.AlgorithmCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AlgorithmRepository {

    private val _cases = MutableStateFlow(DefaultAlgorithmData.cases)
    val cases: StateFlow<List<AlgorithmCase>> = _cases.asStateFlow()

    fun addCustomAlgorithm(case: AlgorithmCase) {
        _cases.update { listOf(case) + it }
    }

    fun removeCustomAlgorithm(id: String) {
        _cases.update { current -> current.filterNot { it.id == id } }
    }
}