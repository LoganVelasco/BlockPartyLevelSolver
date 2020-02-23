package com.solver.levelSolver


var initialState = ArrayList<Char>()

var visitedStates = ArrayList<ArrayList<Char>>()

var badStates = ArrayList<ArrayList<Char>>()

var winsStates = ArrayList<ArrayList<ArrayList<Char>>>()

var moveCount = 0

val solverService = SolverService()

fun solve(state: ArrayList<Char>) {
    moveCount = 0
    initialState = state
    visitedStates = arrayListOf(initialState)
    winsStates = ArrayList()
    badStates = ArrayList()

    val staringPoint = solverService.findBlueBlock(initialState)
    print("Solving")
    solverService.printLayout(initialState)
    Thread.sleep(500)

    getNextMove(staringPoint)
    if (winsStates.isNullOrEmpty())
        solverService.printFailedToFindSolution()
    else
        solverService.printShortestPath()
    println("----------------")
    Thread.sleep(5000)
}


fun getNextMove(position: Int):Boolean {

    var currentState = visitedStates.last()
    var possibleMoves = solverService.getPossibleMoves(position)
    if(isCountHigherThenSolution()){
        solverService.handleDeadEnd(currentState)
        return false
    }
    for (moves in possibleMoves) {
        when (moves) {
            'u' -> {
                moveCount++
                if (makeMove(currentState, position, position - 6)) return true
                moveCount--
            }
            'd' -> {
                moveCount++
                if (makeMove(currentState, position, position + 6)) return true
                moveCount--
            }
            'l' -> {
                moveCount++
                if (makeMove(currentState, position, position - 1)) return true
                moveCount--
            }
            'r' -> {
                moveCount++
                if (makeMove(currentState, position, position + 1)) return true
                moveCount--
            }
            else -> throw Exception()
        }
    }
    solverService.handleDeadEnd(currentState)
    return false
}

fun isCountHigherThenSolution(): Boolean {
    if(winsStates.isNullOrEmpty())return false
    return moveCount  >= winsStates.last().size
}

private fun makeMove(
        currentState: java.util.ArrayList<Char>,
        position: Int,
        newPosition: Int
): Boolean {
    if (newPosition == solverService.findEndingPoint()) {
        solverService.handleWin()
        return false
    }
    val newLayout = getUpdatedLayout(currentState, position, newPosition)
    if (newLayout != null) {
        newLayout[48] = moveCount.toChar()
        visitedStates.add(newLayout)
//        println("Count: ${visitedStates.size} Move Count: $moveCount")
        return getNextMove(newPosition)
    }
    return false
}

private fun getUpdatedLayout(currentState: ArrayList<Char>, position: Int, newPosition: Int): ArrayList<Char>? {
    val newLayout = ArrayList<Char>()
    newLayout.addAll(currentState)

    if(!solverService.isGreenBlockMoved(newLayout, position, newPosition)) {
        newLayout[position] = '.'
        newLayout[newPosition] = 'b'
    }
//    println()
//    print("-----------------------")
//    solverService.printLayout(newLayout)
//    println("Count: $moveCount")
//    println("-----------------------")
//    println()
    return if (solverService.moveRed(newLayout) || solverService.moveRed(newLayout)) {
        null
    } else if (solverService.isAlreadyVisitedState(newLayout)) {
        null
    } else newLayout
}






