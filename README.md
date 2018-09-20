## Connect 4 Minimax/Alpha Beta Pruning Implementation
## David Larson & Caleb Ralphs

### How to run:

Our program uses a few extra classes in addition to those provided by Prof. Heffernan. Firstly, the class that we
created which extends Player is called "Caleb" in the Players package. This class houses our getMove() function and the implementation
of minimax, alpha beta pruning, and our heuristic function. This is what SimplePlayer should be interchanged with
in the RefereeBoard class. Next, we have a class called BoardCJR in the utilities package which extends StateTree. This is the class that we used to implement minimax. We found a need to instantiate objects of type StateTree, so we created the BoardCJR
class which allowed us to do so. Our program will need access to both of these classes in order to function properly.

### Heuristic:

In our program we created a heuristic which evaluated the state of the board in an accelerated manner. We decided 
that checking each piece in the entire board would be a waste of time. To avoid this, we decided to only check each
piece that was exposed to an empty space. For each of these pieces, we looked at the adjacent pieces vertically,
horizontally, and diagonally each way. If 4 in a row was possible (i.e. the 4 surrounding pieces were either that
player's piece or blank), then we counted up the number of that player's piece and squared it. For example, if the
row surrounding an X looked like this:

X _ _ X

then 4 in a row is possible, and there are 2 of our player's piece there, so the function would return 2^2=4. In
the case of this:

X _ _ O _ X

then there is no chance for X to make four in a row, so X would get a value of 0. However, O has a chance to get 4
in a row, and that set of 4 contains 1 of his pieces. Therefore player O would get a value of (1^1)*(-1) = -1.

Then we accumulated all of these values for each player (the opposing player's sum being negative) for the entire 
board, and that was our heuristic value.

Another approach that we discussed for implementing our heuristic function was creating a value matrix corresponding
to the game board. The values within the matrix were associated with the number of 4-in-a-rows that are possible from
that position on the board. Naturally, the positions closer to the middle of the board would be higher than those on
the edge. For a classic 6x7 board, this matrix would look like this:

3 4  5  7  5 4 3

4 6  8 10  8 6 4

5 8 11 13 11 8 5

5 8 11 13 11 8 5

4 6  8 10  8 6 4

3 4  5  7  5 4 3

Given this matrix, evaluating the board becomes as simple as iterating through the gameBoard and checking whether
your player has a piece in each spot. If they do, then you add the value corresponding to that location from the
matrix to the heuristic value for that specific board. The problem we ran into for this was creating the matrix 
for a board of size n. It would have been doable, but a fair amount of work to make it work for variable sized boards. We also anticipated our other idea for a heuristic working better than this one in terms of a competetive edge.

At first, we began trying to implement the first heuristic we discussed. However we ran into some roadblocks in implementing it. Once we got sick of seeing array out-of-bounds errors, we began researching other methods of evaluating a board. That's when we came up with this second idea. It would have been much easier to implement and could have competed well in the class tournament. We finally were able to debug our first heuristic idea and get it to run properly, so we decided to pursue the more robust evaluation function.

