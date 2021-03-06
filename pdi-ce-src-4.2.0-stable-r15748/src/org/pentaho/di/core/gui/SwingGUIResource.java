/*
 * Copyright (c) 2010 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the GNU Lesser General Public License, Version 2.1. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.gnu.org/licenses/lgpl-2.1.txt. The Original Code is Pentaho 
 * Data Integration.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the GNU Lesser Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
 */
package org.pentaho.di.core.gui;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannel;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.plugins.JobEntryPluginType;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.StepPluginType;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;

public class SwingGUIResource {
  private static LogChannelInterface log = new LogChannel("SwingGUIResource");

	private static SwingGUIResource instance;
	
	private Map<String, BufferedImage>	stepImages;
	private Map<String, BufferedImage>	entryImages;
	
	private SwingGUIResource() throws KettleException {
		this.stepImages = loadStepImages();
		this.entryImages = loadEntryImages();
	}
	
	public static SwingGUIResource getInstance() throws KettleException {
		if (instance==null) {
			instance=new SwingGUIResource();
		}
		return instance;
	}
	
	private Map<String, BufferedImage> loadStepImages() throws KettleException {
		Map<String, BufferedImage> map = new HashMap<String, BufferedImage>();
		
		for (PluginInterface plugin: PluginRegistry.getInstance().getPlugins(StepPluginType.class)) {
		  try {
  		  BufferedImage image = getImageIcon(plugin);
  			for (String id : plugin.getIds()) {
  				map.put(id, image);
  			}
		  } catch(Exception e) {
		    log.logError("Unable to load step icon image for plugin: "+plugin.getName()+" (id="+plugin.getIds()[0], e);
		    try { 
		      getImageIcon(plugin); 
		    } catch(Exception ex) {
		      // 
		    }
		  }
		}
		
		return map;
	}
	
	private Map<String, BufferedImage> loadEntryImages() throws KettleException {
		Map<String, BufferedImage> map = new HashMap<String, BufferedImage>();
		
		for (PluginInterface plugin : PluginRegistry.getInstance().getPlugins(JobEntryPluginType.class)) {
		  try {
  			if ("SPECIAL".equals(plugin.getIds()[0])) {
  				continue;
  			}
  			
  			String imageFile = plugin.getImageFile();
  			if (imageFile==null) {
  				throw new KettleException("No image file (icon) specified for plugin: "+plugin);
  			}

  			BufferedImage image = getImageIcon(plugin);
  			if (image==null) {
  				throw new KettleException("Unable to find image file: "+plugin.getImageFile()+" for plugin: "+plugin);
  			}
  			if (image.getHeight(null)<0) {
  				image = getImageIcon(plugin);
  				if (image==null || image.getHeight(null)<0) {
  					throw new KettleException("Unable to load image file: "+plugin.getImageFile()+" for plugin: "+plugin);
  				}
  			}
  			
  			map.put(plugin.getIds()[0], image);
      } catch(Exception e) {
        log.logError("Unable to load job entry icon image for plugin: "+plugin.getName()+" (id="+plugin.getIds()[0], e);
      }
		}
		
		return map;
	}

	private BufferedImage getImageIcon(PluginInterface plugin) throws KettleException {
		try {
		  PluginRegistry registry = PluginRegistry.getInstance();
		  String filename = plugin.getImageFile();

		  ClassLoader classLoader = registry.getClassLoader(plugin);
		  
		  // Try to use the plugin class loader to get access to the icon
		  //
		  InputStream inputStream = classLoader.getResourceAsStream(filename);
			if (inputStream==null) {
				inputStream = classLoader.getResourceAsStream("/"+classLoader);
			}
      // Try to use the PDI class loader to get access to the icon
      //
			if (inputStream==null) {
				inputStream = registry.getClass().getResourceAsStream(plugin.getImageFile());
			}
			if (inputStream==null) {
				inputStream = registry.getClass().getResourceAsStream("/"+plugin.getImageFile());
			}
      // As a last resort, try to use the standard file-system
      //
			if (inputStream==null) {
  			try {
  				inputStream = new FileInputStream(plugin.getImageFile());
  			} catch(FileNotFoundException e) {
  				// Ignore, throws error below
  			}
			}
			
			if (inputStream==null) {
				throw new KettleException("Unable to find file: "+plugin.getImageFile()+" for plugin: "+plugin);
			}
			
			BufferedImage image = ImageIO.read(inputStream);
			inputStream.close();
			
			WaitingImageObserver wia = new WaitingImageObserver(image);
			wia.waitImageLoaded();
			
			return image;
		} catch(Throwable e) {
			throw new KettleException("Unable to load image from file : '"+plugin.getImageFile()+"' for plugin: "+plugin, e);
		}
	}
	
	public Map<String, BufferedImage> getEntryImages() {
		return entryImages;
	}
	
	public Map<String, BufferedImage> getStepImages() {
		return stepImages;
	}
}
