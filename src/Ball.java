
import acm.graphics.GOval;

import java.awt.*;

public class Ball extends GOval {

    public Ball(int x, int y, int diameter) {
        super(diameter, diameter);
        setLocation(x, y);
        setColor(Color.RED);
        setFilled(true);
    }

}
