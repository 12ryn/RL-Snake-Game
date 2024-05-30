
import acm.graphics.GLabel;

import java.awt.*;

public class Instructions extends GLabel {

    public Instructions(String message, double x, double y) {
        super(message, x, y);
        setFont("Arial-20");
        setColor(Color.WHITE);
    }
}
