package com.solver.levelSolver

class SolverService {
    fun moveRed(currentState: ArrayList<Char>): Boolean {
        var possibleMoves = arrayListOf<Char>()
        val blueBlock = findBlueBlock(currentState)
        val redBlock = findRedBlock(currentState)
        val orderedDirections = sortByClosestToGold(redBlock, blueBlock)

        val redMoveFirstDirection = orderedDirections[0]
        val redMoveSecondDirection = orderedDirections[1]

        when (redMoveFirstDirection) {
            'u' -> {
                if (handleRedMove(redBlock, possibleMoves, redMoveFirstDirection, currentState, blueBlock)) return true
            }
            'd' -> {
                if (handleRedMove(redBlock, possibleMoves, redMoveFirstDirection, currentState, blueBlock)) return true
            }
            'l' -> {
                if (handleHorizontalMove(
                                redBlock,
                                possibleMoves,
                                redMoveFirstDirection,
                                currentState,
                                blueBlock,
                                redMoveSecondDirection
                        )
                ) return true
            }
            'r' -> {
                if (handleHorizontalMove(
                                redBlock,
                                possibleMoves,
                                redMoveFirstDirection,
                                currentState,
                                blueBlock,
                                redMoveSecondDirection
                        )
                ) return true
            }
            else -> throw Exception()
        }
        return false
    }

    private fun handleHorizontalMove(
            redBlock: Int,
            possibleMoves: ArrayList<Char>,
            redMoveFirstDirection: Char,
            currentState: ArrayList<Char>,
            blueBlock: Int,
            redMoveSecondDirection: Char
    ): Boolean {
        if (handleRedMove(redBlock, possibleMoves, redMoveFirstDirection, currentState, blueBlock)) return true

        if (!possibleMoves.contains(redMoveFirstDirection) && isMoveCloserToBlue(redMoveSecondDirection, blueBlock, redBlock)) {
            if (redMoveSecondDirection == 'u') {
                if (handleRedMove(redBlock, possibleMoves, redMoveSecondDirection, currentState, blueBlock)) return true

            } else {

                if (handleRedMove(redBlock, possibleMoves, redMoveSecondDirection, currentState, blueBlock)) return true
            }
        }
        return false
    }

    fun isMoveCloserToBlue(redMoveSecondDirection: Char, blueBlock: Int, redBlock: Int): Boolean {
        return when(redMoveSecondDirection){
            'u' -> {
                getRow(blueBlock) != getRow(redBlock)
            }

            'd' -> {
                getRow(blueBlock) != getRow(redBlock)
            }
            else -> throw Exception()
        }
    }

    private fun handleRedMove(
            redBlock: Int,
            possibleMoves: ArrayList<Char>,
            moveDirection: Char,
            currentState: ArrayList<Char>,
            blueBlock: Int
    ): Boolean {

        filterOffGrid(redBlock, possibleMoves, moveDirection)
        filterBlackBlocks(redBlock, possibleMoves, currentState)
        filterGreenBlocksForRed(redBlock, possibleMoves, currentState)
        if (possibleMoves.contains(moveDirection)) {
            val newPosition = getNewPosition(redBlock, moveDirection)
            checkIfOnYellowBlock(redBlock, currentState)
            currentState[newPosition] = 'r'
//            printLayout(currentState)
            if (newPosition == blueBlock) return true
        }

        return false
    }

    fun getNewPosition(redBlock: Int, moveDirection: Char): Int {
        return when (moveDirection) {
            'u' -> {
                redBlock - 6
            }
            'd' -> {
                redBlock + 6
            }
            'l' -> {
                redBlock - 1
            }
            'r' -> {
                redBlock + 1
            }
            else -> throw Exception()
        }
    }

    private fun checkIfOnYellowBlock(redBlock: Int, currentState: ArrayList<Char>) {
        if (redBlock == findEndingPoint()) {
            currentState[redBlock] = 'y'
        } else
            currentState[redBlock] = '.'
    }

    fun isOnSameRow(redBlock: Int, blueBlock: Int): Boolean {
        return if (redBlock > blueBlock) {
            blueBlock + (6 - (blueBlock % 6)) > redBlock
        } else {
            redBlock + (6 - (redBlock % 6)) > blueBlock
        }
    }

    fun findBlueBlock(layout: ArrayList<Char>): Int {
        return layout.indexOf('b')
    }

    fun findRedBlock(layout: ArrayList<Char>): Int {
        return layout.indexOf('r')
    }

    fun findEndingPoint(): Int {
        return initialState.indexOf('y')
    }

    fun getPossibleMoves(position: Int): List<Char> {
        var possibleMoves = arrayListOf<Char>()
        val goldPosition = findEndingPoint()
        val orderedDirections = sortByClosestToGold(position, goldPosition)

        for (direction in orderedDirections) {
            filterOffGrid(position, possibleMoves, direction)
        }
        filterBlackBlocks(position, possibleMoves, visitedStates.last())
        filterGreenBlocks(position, possibleMoves)

        return possibleMoves
    }
    //Past method to when(Move) method
    fun filterGreenBlocks(position: Int, possibleMoves: ArrayList<Char>) {
        val currentState = visitedStates.last()
        var newPossibleMoves =  possibleMoves.clone() as ArrayList<Char>
        for (move in newPossibleMoves) {
            when (move) {
                'u' -> {
                    handleGreenBlocks(currentState, position, position - 6, possibleMoves, 'u')
                }
                'd' -> {
                    handleGreenBlocks(currentState, position, position + 6, possibleMoves, 'd')
                }
                'l' -> {
                    handleGreenBlocks(currentState, position, position - 1, possibleMoves, 'l')
                }
                'r' -> {
                    handleGreenBlocks(currentState, position, position + 1, possibleMoves, 'r')
                }
                else -> throw Exception()
            }
        }
    }

    private fun handleGreenBlocks(
            currentState: ArrayList<Char>,
            position: Int,
            newPosition: Int,
            possibleMoves: ArrayList<Char>,
            move: Char
    ) {
        val validMoves = ArrayList<Char>()
        val greenBlockNewPosition = getNewGreenPosition(position, newPosition)

        if (currentState[newPosition] == 'g') {
            filterOffGrid(newPosition, validMoves, move)
            filterBlackBlocks(newPosition, validMoves, currentState)
//        if((greenBlockNewPosition in 0..47) && currentState[greenBlockNewPosition] == 'g')
//            handleGreenBlocks(currentState, newPosition, greenBlockNewPosition, possibleMoves, move)
            if (validMoves.size != 1 || currentState[greenBlockNewPosition] == 'r' ) {
                possibleMoves.remove(move)
            }
        }
    }

    fun isGreenBlockMoved(newLayout: ArrayList<Char>, position: Int, newPosition: Int): Boolean{
        return if(newLayout[newPosition] == 'g'){
            val newGreenPosition = getNewGreenPosition(position, newPosition)
            newLayout[newPosition] = newLayout[position]
            newLayout[position] = '.'
            if(newLayout[newGreenPosition] != 'y') {
                if (newLayout[newGreenPosition] == 'g') {
                    newLayout[newGreenPosition] = '.'
                } else
                    newLayout[newGreenPosition] = 'g'
            }
            true
        }else false
    }

    fun getNewGreenPosition(position: Int, newPosition: Int): Int {
        return  position - ((position - newPosition) + (position - newPosition))
    }

    fun isAlreadyVisitedState(state: ArrayList<Char>): Boolean {
        var oldCount = 0;
        var newCount = 0;
        for (oldState in visitedStates) {
            oldCount = oldState[48].toInt()
            newCount = state[48].toInt()
            oldState[48] = 0.toChar()
            state[48] = 0.toChar()
            val isStateSame = oldState == state

            if (isStateSame ){  // TODO Make more efficient for lots of winsStates
                if(!winsStates.isNullOrEmpty() && winsStates.last().contains(state)){
                    if(oldCount <= newCount ) {
                        oldState[48] = oldCount.toChar()
                        state[48] = newCount.toChar()
                        return true
                    }
                }else {
                    oldState[48] = oldCount.toChar()
                    state[48] = newCount.toChar()
                    return true
                }
            }
            oldState[48] = oldCount.toChar()
            state[48] = newCount.toChar()
        }
        return false
    }



    fun filterBlackBlocks(position: Int, possibleMoves: ArrayList<Char>, currentState: ArrayList<Char>) {


        if (position - 6 >= 0 && (currentState[position - 6] == 'x' || currentState[position - 6] == 'r')) {
            possibleMoves.remove('u')
        }

        if (position + 6 < 48 && (currentState[position + 6] == 'x' || currentState[position + 6] == 'r')) {
            possibleMoves.remove('d')
        }

        if (position - 1 >= 0 && (currentState[position - 1] == 'x' || currentState[position - 1] == 'r')) {
            possibleMoves.remove('l')
        }

        if (position + 1 < 48 && (currentState[position + 1] == 'x' || currentState[position + 1] == 'r')) {
            possibleMoves.remove('r')
        }
    }

    fun filterGreenBlocksForRed(position: Int, possibleMoves: ArrayList<Char>, currentState: ArrayList<Char>) {

        if (position - 6 >= 0 && (currentState[position - 6] == 'g')) {
            possibleMoves.remove('u')
        }

        if (position + 6 < 48 && (currentState[position + 6] == 'g')) {
            possibleMoves.remove('d')
        }

        if (position - 1 >= 0 && (currentState[position - 1] == 'g')) {
            possibleMoves.remove('l')
        }

        if (position + 1 < 48 && (currentState[position + 1] == 'g')) {
            possibleMoves.remove('r')
        }
    }

    //Refactor to closest to point
    fun sortByClosestToGold(position: Int, goldPosition: Int): ArrayList<Char> {
        val orderedDirections = ArrayList<Char>()

        if (getColoum(position) == getColoum(goldPosition)) {
            if (position < goldPosition) {
                orderedDirections.add('d')
                orderedDirections.add('r')
                orderedDirections.add('l')
                orderedDirections.add('u')
            } else {
                orderedDirections.add('u')
                orderedDirections.add('r')
                orderedDirections.add('l')
                orderedDirections.add('d')
            }
        } else if (getColoum(position) < getColoum(goldPosition)) {
            orderedDirections.add('r')
            if (position < goldPosition) {
                orderedDirections.add('d')
                orderedDirections.add('u')
            } else {
                orderedDirections.add('u')
                orderedDirections.add('d')
            }
            orderedDirections.add('l')
        } else {
            orderedDirections.add('l')
            if (position < goldPosition) {
                orderedDirections.add('d')
                orderedDirections.add('u')
            } else {
                orderedDirections.add('u')
                orderedDirections.add('d')
            }
            orderedDirections.add('r')
        }
        return orderedDirections
    }

    fun getColoum(position:  Int): Int {
        return position % 6
    }

    fun getRow(position: Int): Any {
        return  position / 6
    }

    fun filterOffGrid(position: Int, possibleMoves: ArrayList<Char>, direction: Char) {
        when (direction) {
            'u' -> {
                if (position - 6 > 0) {
                    possibleMoves.add('u')
                }
            }
            'd' -> {
                if (position + 6 < 48) {
                    possibleMoves.add('d')
                }
            }
            'l' -> {
                if ((position) % 6 != 0) {
                    possibleMoves.add('l')
                }
            }
            'r' -> {
                if ((position + 1) % 6 != 0) {
                    possibleMoves.add('r')
                }
            }
            else -> throw Exception()
        }
    }

    fun printLayout(layout: ArrayList<Char>) {
        for (x in 0..47) {
            if (x % 6 == 0) println()
            print(layout[x] + "  ")
        }
        println()
        println()
    }

    fun handleDeadEnd(
            currentState: java.util.ArrayList<Char>
    ) {
//        println("-----------")
//        println("Hit Dead End")
        if (currentState != initialState) badStates.add(currentState)
//        println("ADDING STATE TO DEAD END STATES")

//        println("RETURNING TO:")
//        printLayout(visitedStates[visitedStates.size - 3])
//        println("-----------")
    }

    fun handleWin() {
        var copy = visitedStates.clone() as ArrayList<ArrayList<Char>>
        copy.removeAll(badStates)
        if(copy.size != moveCount) return
        winsStates.add(copy)

//        println("-----------")
        println("SOLVED in $moveCount moves!")
        println("Looking for shorter solutions")
        println()

//        println("PRINTING SOLUTION")
//        Thread.sleep(3000)
//
//        for (layout in copy) {
//            Thread.sleep(500)
//            printLayout(layout)
//        }

        //exitProcess(0)
    }

    fun printFailedToFindSolution() {
        println()
        println("No Solution Found")
        println("--------------------")
    }

    fun printShortestPath() {
        println()
        println("SHORTEST PATH IS ${winsStates[winsStates.size-1].size} MOVES")
//        Thread.sleep(1500)

        for (layout in winsStates[winsStates.size-1]) {
            printLayout(layout)
//            Thread.sleep(1000)
        }
        println("-----------")
        println("SOLVED in ${winsStates[winsStates.size-1].size}!")
        println("-----------")
    }
}