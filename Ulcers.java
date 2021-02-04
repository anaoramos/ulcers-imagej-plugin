import java.awt.Rectangle;
import java.awt.Window;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.*;
import ij.plugin.filter.PlugInFilter;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;


public class Ulcers implements PlugInFilter {

	public int setup(String arg, ImagePlus imp) {
		if (arg.equals("about")){
		return DONE;}
		return DOES_RGB; 	}

	public void run(ImageProcessor ip) {
		ImageProcessor prc=ip.duplicate();

		ImagePlus iprc= new ImagePlus("Image", prc);


		byte [][] rgb;
		ColorProcessor cp = (ColorProcessor) prc;
		double totalpixeis=0;
		double red=0;
		double blue=0;
		double black=0;
		double yellow=0;

	
		int h = iprc.getHeight();
		int w = iprc.getWidth();
		rgb = new byte[3][w*h];
		cp.getRGB(rgb[0], rgb[1], rgb[2]);
		
		Rectangle roi = ip.getRoi();

	// getMask: retorna mask se for roi irregular; null se regular
		ImageProcessor mask = ip.getMask();
	//irregular
		boolean roiIrr= (mask != null);
		
		
		
		int offset,i;
		for (int y=roi.y; y<roi.y+roi.height; y++) {
			offset = y*w;
			for (int x=roi.x; x<roi.x+roi.width; x++) {
				i= offset+x;
				if (!roiIrr || mask.getPixel(x-roi.x, y-roi.y) > 0) {

					//red
					if((rgb[0][i] & 0xff)>=72 && (rgb[1][i] & 0xff) >=0 && (rgb[2][i] & 0xff) >=0 && (rgb[0][i] & 0xff)<=255 && (rgb[1][i] & 0xff) <=80 && (rgb[2][i] & 0xff) <=100){
						rgb[0][i] = (byte) (255 & 0xff);
						rgb[1][i] = 0;
						rgb[2][i] = 0;
						red=red+1;
						totalpixeis=totalpixeis+1;
					}
					//blue
					if((rgb[0][i] & 0xff)>125 && (rgb[1][i] & 0xff) >130 && (rgb[2][i] & 0xff) >=143){
						rgb[0][i] = 0;
						rgb[1][i] = 0;
						rgb[2][i] = (byte) (255 & 0xff);
						blue=blue+1;
						totalpixeis=totalpixeis+1;
		
					}
					


					//black
					if((rgb[0][i] & 0xff)<=70 && (rgb[1][i] & 0xff) <=75 && (rgb[2][i] & 0xff) <=52){
						rgb[0][i] = 0;
						rgb[1][i] = 0;
						rgb[2][i] = 0;
						black=black+1;
						totalpixeis=totalpixeis+1;
					
					}
									
					//yellow
					if((rgb[0][i] & 0xff)>=105 && (rgb[1][i] & 0xff) >80 && (rgb[2][i] & 0xff) >= 13 && (rgb[0][i] & 0xff)<=240 && (rgb[1][i] & 0xff) <=215 && (rgb[2][i] & 0xff) <=145){
			
						rgb[0][i] = (byte) (255 & 0xff);
						rgb[1][i] = (byte) (255 & 0xff);
						rgb[2][i] = 0;
						yellow=yellow+1;
						totalpixeis=totalpixeis+1;
					}
									
				}
			}
		}

		//Percentages

		double per_red = (red/totalpixeis)*100;
		double per_blue = (blue/totalpixeis)*100;
		double per_black = (black/totalpixeis)*100;
		double per_yellow = (yellow/totalpixeis)*100;

		
		String pRed = String.format("%.2f", per_red);
		String pBlue = String.format("%.2f", per_blue);
		String pBlack = String.format("%.2f", per_black);
		String pYellow = String.format("%.2f", per_yellow);


		

		ImagePlus foot = NewImage.createRGBImage("Diabetic Foot", w, h, 1, NewImage.FILL_BLACK);
		ImageProcessor foot_ip = foot.getProcessor();
		ColorProcessor foot_col = (ColorProcessor) foot_ip;
		foot_col.setRGB(rgb[0], rgb[1], rgb[2]);
		foot.show();
		IJ.showMessage("Percentages", ("\n \n Necrosis: " + pBlack + "%;\n \n	Fibrine: " + pYellow + "%;\n \n	Granulation: "+pRed + "%;\n \n	Unknown:" +pBlue + "%\n \n"));

	}
}

