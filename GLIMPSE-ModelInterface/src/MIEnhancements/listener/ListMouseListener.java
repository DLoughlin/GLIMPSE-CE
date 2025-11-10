package listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

/**
 * ListMouseListener handles double-click events on a JList to clear its selection.
 * <p>
 * Author: TWU
 * Date: 1/2/2016
 */
public class ListMouseListener extends MouseAdapter {
    /**
     * The JList instance to listen for mouse events.
     */
    private final JList<String> list;

    /**
     * Constructs a ListMouseListener for the specified JList.
     * @param list the JList to attach the listener to
     */
    public ListMouseListener(JList<String> list) {
        this.list = list;
    }

    /**
     * Invoked when a mouse button has been pressed on the JList.
     * Clears the selection if the event is a double-click.
     * @param e the MouseEvent
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Check for double-click
        if (e.getClickCount() == 2) {
            // Temporarily mark selection as adjusting
            list.getSelectionModel().setValueIsAdjusting(true);
            // Clear all selections
            list.clearSelection();
            // Refresh the UI to reflect changes
            list.updateUI();
        }
    }
}