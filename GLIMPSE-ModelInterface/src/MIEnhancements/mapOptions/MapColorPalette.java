package mapOptions;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import org.geotools.brewer.color.ColorBrewer;
import org.geotools.brewer.color.BrewerPalette;

public class MapColorPalette implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Color[] colors;
	private String description = "";
	private boolean reverseColors;
	
	public MapColorPalette(MapColorPalette pal) {
		colors = new Color[pal.colors.length];
		System.arraycopy(pal.colors, 0, colors, 0, pal.colors.length);
		this.description = pal.description;
		this.reverseColors = pal.reverseColors;
	}

	public MapColorPalette(Color[] colors, String description, boolean reverseColors) {
		this.colors = colors;
		this.description = description;
		this.reverseColors = reverseColors;
	}

	public String getDescription() {
		return description;
	}

	public Color[] getColors() {
		if (!reverseColors) {
			return colors;
		} else {
			return reverseColorMap();
		}
	}

	public Color[] getOriginalColors() {
		return colors;
	}
	
	public String getHexColor(Color color) {
		return "#"+Integer.toHexString(color.getRGB()).substring(2).toUpperCase();
	}

	private Color[] reverseColorMap() {
		List<Color> reversedColors = new ArrayList<Color>();
		reversedColors.addAll(Arrays.asList(colors));
		Collections.reverse(reversedColors);
		
		return reversedColors.toArray(new Color[0]);
	}

	public void setColor(int index, Color color) {
		this.colors[index] = color;
	}

	public int getColorCount() {
		return colors.length;
	}

	public Color getColor(int index) {
		return colors[(!reverseColors ? index : getColorCount() - 1 - index)];
	}

	public static MapColorPalette getDefaultPalette() {

		ColorBrewer brewer = new ColorBrewer();	// calls to GeoTools library

		brewer.loadPalettes();
		BrewerPalette palette = brewer.getPalettes(ColorBrewer.DIVERGING)[4];
		return new MapColorPalette(palette.getColors(10), palette.getDescription(), true);
	}

	public static MapColorPalette getMapColorPalette(String colorType, int nChoice, int nClass, boolean reverseColors ) {
		
		int maxAllowed;
		ColorBrewer brewer = new ColorBrewer();	// calls to GeoTools library
		brewer.loadPalettes();
		BrewerPalette palette = brewer.getPalettes(ColorBrewer.DIVERGING)[4];
		
		if (colorType.equalsIgnoreCase("DIVERGING")) {
		    palette = brewer.getPalettes(ColorBrewer.DIVERGING)[nChoice];
		    maxAllowed = palette.getMaxColors();
		    
		    if (nClass>maxAllowed) {
		    JOptionPane.showMessageDialog(null, "this color palette only allows maximum of " + maxAllowed + " colors.");
		    return new MapColorPalette(palette.getColors(maxAllowed), palette.getDescription(), !reverseColors);
		    }else {
				//YD edits,August-2024,change the center color into white
				// when nClass is an odd number
				if(nClass % 2 ==0) {
		           return new MapColorPalette(palette.getColors(nClass), palette.getDescription(), !reverseColors);
				}else {
					Color[] newColors = palette.getColors(nClass);
//					for (int i=0;i<newColors.length;i++) {
//				    	System.out.println("check each color in color palette before change:"+newColors[i]);
//				    }
					int idx2Change = ((nClass+1)/2)-1;
					newColors[idx2Change] = Color.WHITE;
				   
//					for (int i=0;i<newColors.length;i++) {
//				    	System.out.println("check each color in color palette after change:"+newColors[i]);
//				    }
				    return new MapColorPalette(newColors, palette.getDescription(), !reverseColors);
				}
		    }
		}else if(colorType.equalsIgnoreCase("QUALITATIVE")){
			palette = brewer.getPalettes(ColorBrewer.QUALITATIVE)[nChoice];
			maxAllowed = palette.getMaxColors();
		    if (nClass>maxAllowed) {
		    JOptionPane.showMessageDialog(null, "this color palette only allows maximum of " + maxAllowed + " colors.");
		    return new MapColorPalette(palette.getColors(maxAllowed), palette.getDescription(), reverseColors);
		    }else {
			return new MapColorPalette(palette.getColors(nClass), palette.getDescription(), reverseColors);
		    }
		}else if(colorType.equalsIgnoreCase("SEQUENTIAL")) {
			palette = brewer.getPalettes(ColorBrewer.SEQUENTIAL)[nChoice];
			maxAllowed = palette.getMaxColors();
		    if (nClass>maxAllowed) {
		    JOptionPane.showMessageDialog(null, "this color palette only allows maximum of " + maxAllowed + " colors.");
		    return new MapColorPalette(palette.getColors(maxAllowed), palette.getDescription(), reverseColors);
		    }else {
			return new MapColorPalette(palette.getColors(nClass), palette.getDescription(), reverseColors);
		    }
		}else {
			palette = brewer.getPalettes(ColorBrewer.DIVERGING)[4];	
			return new MapColorPalette(palette.getColors(nClass), palette.getDescription(), !reverseColors);
		}
		
		
	}
	
	
	
	public void setReverseColors(boolean reverseColors) {
		this.reverseColors = reverseColors;
	}

	public boolean isReverseColors() {
		return reverseColors;
	}
}

