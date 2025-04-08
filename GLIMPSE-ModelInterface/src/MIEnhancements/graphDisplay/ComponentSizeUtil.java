/*
* LEGAL NOTICE
* This computer software was prepared by US EPA.
* THE GOVERNMENT MAKES NO WARRANTY, EXPRESS OR IMPLIED, OR ASSUMES ANY
* LIABILITY FOR THE USE OF THIS SOFTWARE. This notice including this
* sentence must appear on any copies of this computer software.
* 
* EXPORT CONTROL
* User agrees that the Software will not be shipped, transferred or
* exported into any country or used in any manner prohibited by the
* United States Export Administration Act or any other applicable
* export laws, restrictions or regulations (collectively the "Export Laws").
* Export of the Software may require some form of license or other
* authority from the U.S. Government, and failure to obtain such
* export control license may result in criminal liability under
* U.S. laws. In addition, if the Software is identified as export controlled
* items under the Export Laws, User represents and warrants that User
* is not a citizen, or otherwise located within, an embargoed nation
* (including without limitation Iran, Syria, Sudan, Cuba, and North Korea)
*     and that User is not otherwise prohibited
* under the Export Laws from receiving the Software.
*
* SUPPORT
* For the GLIMPSE project, GCAM development, data processing, and support for 
* policy implementations has been led by Dr. Steven J. Smith of PNNL, via Interagency 
* Agreements 89-92423101 and 89-92549601. Contributors * from PNNL include 
* Maridee Weber, Catherine Ledna, Gokul Iyer, Page Kyle, Marshall Wise, Matthew 
* Binsted, and Pralit Patel. Coding contributions have also been made by Aaron 
* Parks and Yadong Xu of ARA through the EPA’s Environmental Modeling and 
* Visualization Laboratory contract. 
* 
*/
package graphDisplay;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.stream.Stream;

public class ComponentSizeUtil {

	/**
	 * getScreenInsets, This returns the insets of the screen, which are defined
	 * by any task bars that have been set up by the user. This function
	 * accounts for multi-monitor setups. If a window is supplied, then the the
	 * monitor that contains the window will be used. If a window is not
	 * supplied, then the primary monitor will be used.
	 */
	static public Insets getScreenInsets(Window windowOrNull) {
		Insets insets;
		if (windowOrNull == null) {
			insets = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().getDefaultConfiguration());
		} else {
			insets = windowOrNull.getToolkit().getScreenInsets(windowOrNull.getGraphicsConfiguration());
		}
		return insets;
	}

	/**
	 * getScreenWorkingArea, This returns the working area of the screen. (The
	 * working area excludes any task bars.) This function accounts for
	 * multi-monitor setups. If a window is supplied, then the the monitor that
	 * contains the window will be used. If a window is not supplied, then the
	 * primary monitor will be used.
	 */
	static public Rectangle getScreenWorkingArea(Window windowOrNull) {
		Insets insets;
		Rectangle bounds;
		if (windowOrNull == null) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			insets = Toolkit.getDefaultToolkit().getScreenInsets(ge.getDefaultScreenDevice().getDefaultConfiguration());
			bounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		} else {
			GraphicsConfiguration gc = windowOrNull.getGraphicsConfiguration();
			insets = windowOrNull.getToolkit().getScreenInsets(gc);
			bounds = gc.getBounds();
		}
		bounds.x += insets.left;
		bounds.y += insets.top;
		bounds.width -= (insets.left + insets.right);
		bounds.height -= (insets.top + insets.bottom);
		return bounds;
	}

	/**
	 * getScreenTotalArea, This returns the total area of the screen. (The total
	 * area includes any task bars.) This function accounts for multi-monitor
	 * setups. If a window is supplied, then the the monitor that contains the
	 * window will be used. If a window is not supplied, then the primary
	 * monitor will be used.
	 */
	static public Rectangle getScreenTotalArea(Window windowOrNull) {
		Rectangle bounds;
		if (windowOrNull == null) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			bounds = ge.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
		} else {
			GraphicsConfiguration gc = windowOrNull.getGraphicsConfiguration();
			bounds = gc.getBounds();
		}
		return bounds;
	}

	/*
	 * returns the x position of the right most edge of the right most screen.
	 * If no screens are found, then it returns 0.
	 */

	public static int getCurScreenConner() {
		GraphicsDevice devices[];
		devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		return Stream.of(devices).map(GraphicsDevice::getDefaultConfiguration).map(GraphicsConfiguration::getBounds)
				.mapToInt(bounds -> bounds.x + bounds.width).max().orElse(0);
	}

	static public Rectangle getScreenBounds(Window wnd) {
		Rectangle sb;
		Insets si = getScreenInsets(wnd);

		if (wnd == null) {
			sb = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration()
					.getBounds();
		} else {
			sb = wnd.getGraphicsConfiguration().getBounds();
		}

		sb.x += si.left;
		sb.y += si.top;
		sb.width -= si.left + si.right;
		sb.height -= si.top + si.bottom;
		return sb;
	}

	static public Insets getScreenInsets(Window wnd, int i) {
		Insets si;

		if (wnd == null) {
			si = Toolkit.getDefaultToolkit().getScreenInsets(GraphicsEnvironment.getLocalGraphicsEnvironment()
					.getDefaultScreenDevice().getDefaultConfiguration());
		} else {
			si = wnd.getToolkit().getScreenInsets(wnd.getGraphicsConfiguration());
		}
		return si;
	}

	/*
	 * enumerate the graphics devices on the system (if multiple monitors are
	 * installed), and you can use that information to determine monitor
	 * affinity or automatic placement (some systems use a little side monitor
	 * for real-time displays while an app is running in the background, and
	 * such a monitor can be identified by size, screen colors, etc.):
	 */

	public static void testMonitors(int myWidth, int myHeight, int minRequiredWidth, int minRequiredHeight) {
		// Test if each monitor will support my app's window
		// Iterate through each monitor and see what size each is
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();
		Dimension mySize = new Dimension(myWidth, myHeight);
		Dimension maxSize = new Dimension(minRequiredWidth, minRequiredHeight);
		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			// Update the max size found on this monitor
			if (dm.getWidth() > maxSize.getWidth() && dm.getHeight() > maxSize.getHeight()) {
				maxSize.setSize(dm.getWidth(), dm.getHeight());
			}

			// Do test if it will work here
			if (mySize.width<maxSize.width &&mySize.height<maxSize.height)
				;
		}
	}

	public Rectangle getCurrentScreenBounds(Component component) {
		return component.getGraphicsConfiguration().getBounds();
	}

	public static double[] getCurScreenSize() {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		return new double[] { screenSize.getWidth(), screenSize.getHeight() };
	}

	public static double[] getCurMulScreenSize() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return new double[] { gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight() };
	}

	public static int getCurResolution() {
		return Toolkit.getDefaultToolkit().getScreenResolution();
	}

	public static Dimension getCurMulResolution() {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		return gd.getFullScreenWindow().getSize();
	}

	public static Font getRightFontSize(String name, int style, int height, Graphics g) {
		int size = height;
		Boolean up = null;
		while (true) {
			Font font = new Font(name, style, size);
			int testHeight = g.getFontMetrics(font).getHeight();
			if (testHeight < height && up != Boolean.FALSE) {
				size++;
				up = Boolean.TRUE;
			} else if (testHeight > height && up != Boolean.TRUE) {
				size--;
				up = Boolean.FALSE;
			} else {
				return font;
			}
		}
	}

	/*
	 * getFontRenderedHeight
	 * *************************************************************************
	 * Summary: Font metrics do not give an accurate measurement of the rendered
	 * font height for certain strings because the space between the ascender
	 * limit and baseline is not always fully used and descenders may not be
	 * present. for example the strings '0' 'a' 'f' and 'j' are all different
	 * heights from top to bottom but the metrics returned are always the same.
	 * If you want to place text that exactly fills a specific height, you need
	 * to work out what the exact height is for the specific string. This method
	 * achieves that by rendering the text and then scanning the top and bottom
	 * rows until the real height of the string is found.
	 */
	/**
	 * Calculate the actual height of rendered text for a specific string more
	 * accurately than metrics when ascenders and descenders may not be present
	 * <p>
	 * Note: this method is probably not very efficient for repeated measurement
	 * of large strings and large font sizes but it works quite effectively for
	 * short strings. Consider measuring a subset of your string value. Also
	 * beware of measuring symbols such as '-' and '.' the results may be
	 * unexpected!
	 * 
	 * @param string
	 *            The text to measure. You might be able to speed this process
	 *            up by only measuring a single character or subset of your
	 *            string i.e if you know your string ONLY contains numbers and
	 *            all the numbers in the font are the same height, just pass in
	 *            a single digit rather than the whole numeric string.
	 * @param font
	 *            The font being used. Obviously the size of the font affects
	 *            the result
	 * @param targetGraphicsContext
	 *            The graphics context the text will actually be rendered in.
	 *            This is passed in so the rendering options for anti-aliasing
	 *            can be matched.
	 * @return Integer - the exact actual height of the text.
	 * @author Robert Heritage [mrheritage@gmail.com]
	 */
	public Integer getFontRenderedHeight(String string, Font font, Graphics2D targetGraphicsContext) {
		BufferedImage image;
		Graphics2D g;
		Color textColour = Color.white;

		// In the first instance; use a temporary BufferedImage object to render
		// the text and get the font metrics.
		image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		g = image.createGraphics();
		FontMetrics metrics = g.getFontMetrics(font);
		Rectangle2D rect = metrics.getStringBounds(string, g);

		// now set up the buffered Image with a canvas size slightly larger than
		// the font metrics - this guarantees that there is at least one row of
		// black pixels at the top and the bottom
		image = new BufferedImage((int) rect.getWidth() + 1, (int) metrics.getHeight() + 2, BufferedImage.TYPE_INT_RGB);
		g = image.createGraphics();

		// take the rendering hints from the target graphics context to ensure
		// the results are accurate.
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				targetGraphicsContext.getRenderingHint(RenderingHints.KEY_ANTIALIASING));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				targetGraphicsContext.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING));

		g.setColor(textColour);
		g.setFont(font);
		g.drawString(string, 0, image.getHeight());

		// scan the bottom row - descenders will be cropped initially, so the
		// text will need to be moved up (down in the co-ordinates system) to
		// fit it in the canvas if it contains any. This may need to be done a
		// few times until there is a row of black pixels at the bottom.
		boolean foundBottom, foundTop = false;
		int offset = 0;
		do {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, image.getWidth(), image.getHeight());
			g.setColor(textColour);
			g.drawString(string, 0, image.getHeight() - offset);

			foundBottom = true;
			for (int x = 0; x < image.getWidth(); x++) {
				if (image.getRGB(x, image.getHeight() - 1) != Color.BLACK.getRGB()) {
					foundBottom = false;
				}
			}
			offset++;
		} while (!foundBottom);

		//System.out.println(image.getHeight());

		// Scan the top of the image downwards one line at a time until it
		// contains a non-black pixel. This loop uses the break statement to
		// stop the while loop as soon as a non-black pixel is found, this
		// avoids the need to scan the rest of the line
		int y = 0;
		do {
			for (int x = 0; x < image.getWidth(); x++) {
				if (image.getRGB(x, y) != Color.BLACK.getRGB()) {
					foundTop = true;
					break;
				}
			}
			y++;
		} while (!foundTop);

		return image.getHeight() - y;
	}

	/*
	 * Compute Text Dimension.
	 */
	public static Dimension GetTextDimension(Font font, String text) {
		//System.out.println("GetTextDimension:g "+font.getSize()+"  : "+text);
		BufferedImage image;
		Graphics2D g;
		//Color textColour = Color.white;

		// In the first instance; use a temporary BufferedImage object to render
		// the text and get the font metrics.
		image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		g = image.createGraphics();
		// get metrics from the graphics
		FontMetrics metrics = g.getFontMetrics(font);
		// get the height of a line of text in this
		// font and render context
		int hgt = metrics.getHeight();
		// get the advance of my text in this font
		// and render context
		int adv = metrics.stringWidth(text);
		// calculate the size of a box to hold the
		// text with some padding.
		return new Dimension(adv+2, hgt+2);
	}
	
	/*
	 * The font height in Java (and many other places) is given in
	 * "typographic points", which are defined as roughly 1/72nd of an inch.
	 */
	public static double GetFontPoint(int pixelSize) {
		return pixelSize * Toolkit.getDefaultToolkit().getScreenResolution() / 72.0;
	}

	/*
	 * To set the ascent+descent to the pixel size
	 */
	public static double GetAcentFontPoint(int fontSize, Font font, Graphics g) {
		FontMetrics m = g.getFontMetrics(font); // g is your current Graphics
												// object
		return fontSize * (m.getAscent() + m.getDescent()) / m.getAscent();
	}

	public static int getStringWidth(Component c, String s) {
		Graphics g = c.getGraphics();
		g.setFont(new Font("Serif", Font.BOLD, 24));
		return g.getFontMetrics().stringWidth(s);
	}
}
