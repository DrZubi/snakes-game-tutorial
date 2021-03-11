package max;

import snakes.Bot;
import snakes.Coordinate;
import snakes.Direction;
import snakes.Snake;

import java.util.Arrays;
import java.util.Iterator;

/**
 * A starter implementation to experiment with making bots
 */
public class StarterBot implements Bot {
    private static final Direction[] DIRECTIONS = {Direction.UP, Direction.DOWN, Direction.LEFT, Direction.RIGHT};

    @Override
    public Direction chooseDirection(Snake snake, Snake opponent, Coordinate mazeSize, Coordinate apple) {
        final Coordinate head = snake.getHead();

        // Get the coordinate of the second element of the snake's body
        Coordinate afterHeadNotFinal = null;
        if (snake.body.size() >= 2) {
            Iterator<Coordinate> it = snake.body.iterator();
            it.next();
            afterHeadNotFinal = it.next();
        }
        final Coordinate afterHead = afterHeadNotFinal;

        final Coordinate opponentHead = opponent.getHead();

        // Get coordinates immediately around the opponent's head
        final Coordinate[] opponentHeadAround = {
                Coordinate.add(opponentHead, new Coordinate(0, -1)),
                Coordinate.add(opponentHead, new Coordinate(0, 1)),
                Coordinate.add(opponentHead, new Coordinate(-1, 0)),
                Coordinate.add(opponentHead, new Coordinate(1, 0))
        };

        // Prevent the snake from going backwards
        Direction[] validMoves = Arrays.stream(DIRECTIONS)
                .filter(d -> !head.moveTo(d).equals(afterHead)) // Filter out the backwards move
                .sorted()
                .toArray(Direction[]::new);

        // Find directions that don't result in death
        Direction[] notLosing = Arrays.stream(validMoves)
                .filter(d -> head.moveTo(d).inBounds(mazeSize))             // Don't leave maze
                .filter(d -> !opponent.elements.contains(head.moveTo(d)))   // Don't collide with opponent...
                .filter(d -> !snake.elements.contains(head.moveTo(d)))      // and yourself
                .sorted()
                .toArray(Direction[]::new);

        // Find directions that don't result in imminent death
        Direction[] notBad = Arrays.stream(notLosing)
                .filter(d -> !Arrays.asList(opponentHeadAround).contains(head.moveTo(d)))
                .sorted()
                .toArray(Direction[]::new);


        // Get the coordinate of the second element of the opponent's body
//        Coordinate opponentAfterHeadNotFinal = null;
//        if (opponent.body.size() >= 2) {
//            Iterator<Coordinate> it = snake.body.iterator();
//            it.next();
//            opponentAfterHeadNotFinal = it.next();
//        }
//
//        final Coordinate opponentAfterHead = opponentAfterHeadNotFinal;

        // Get coordinate to move towards apple
        final Direction appleMove = getDirection(head, apple);

        // Go for apple
        if (Arrays.asList(notBad).contains(appleMove))
            return appleMove;
        // Need to make decent move
        else if (notBad.length > 0)
            return notBad[0];
        // Need to survive
        else if (notLosing.length > 0)
            return notLosing[0];
        // Cannot avoid loss
        else
            return validMoves[0];
    }

    /**
     * Get best direction from current point to other point on map
     * @param to point to move to
     * @param from point to move from
     * @return direction
     */
    private Direction getDirection(Coordinate to, Coordinate from) {
        final Coordinate vector = new Coordinate(to.x - from.x, to.y - from.y);
        int x, y;

        if (Math.abs(vector.x) >= Math.abs(vector.y)) {
            y = 0;
            x = - vector.x / Math.abs(vector.x);
        } else {
            x = 0;
            y = - vector.y / Math.abs(vector.y);
        }

        for (Direction direction : Direction.values())
            if (direction.dx == x && direction.dy == y)
                return direction;
        return null;
    }
}
