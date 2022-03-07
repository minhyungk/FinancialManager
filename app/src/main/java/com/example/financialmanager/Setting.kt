package com.example.financialmanager

data class Setting(
    var hideProduct: Boolean ?= false,
    var hideCategory: Boolean ?= false,
    var hidePrice: Boolean ?= false)
