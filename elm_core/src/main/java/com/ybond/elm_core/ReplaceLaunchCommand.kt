package com.ybond.elm_core

/**
 * Marker interface for [Actor] command
 * Before execution previous command with same [commandId] will be cancelled
 * */
interface ReplaceLaunchCommand {
    val commandId: String
}