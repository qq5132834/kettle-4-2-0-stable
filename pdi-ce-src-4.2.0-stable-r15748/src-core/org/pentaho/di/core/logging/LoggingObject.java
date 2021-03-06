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
package org.pentaho.di.core.logging;

import java.util.Date;

import org.pentaho.di.core.Const;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.ObjectRevision;
import org.pentaho.di.repository.RepositoryDirectory;
import org.pentaho.di.repository.RepositoryDirectoryInterface;

public class LoggingObject implements LoggingObjectInterface {
	
	private String logChannelId;
	private LoggingObjectType objectType;
	private String objectName;
	private String objectCopy;
	private RepositoryDirectoryInterface repositoryDirectory;
	private String filename;
	private ObjectId objectId;
	private ObjectRevision objectRevision;
	private LogLevel logLevel = DefaultLogLevel.getLogLevel();
	
	private String containerObjectId;
	
	private LoggingObjectInterface parent;
	
	private Date registrationDate;
	
	public LoggingObject(Object object) {
		if (object instanceof LoggingObjectInterface) grabLoggingObjectInformation((LoggingObjectInterface)object);
		else grabObjectInformation(object);
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof LoggingObject)) return false;
		if (obj == this) return true;
		
		try {
			LoggingObject loggingObject = (LoggingObject) obj;
	
		  // No carte object id specified on either side OR the same carte object id means: the same family.
			//
			boolean sameCarteFamily = 
			  (getContainerObjectId()==null && loggingObject.getContainerObjectId()==null) || 
			  (getContainerObjectId()!=null && loggingObject.getContainerObjectId()!=null && getContainerObjectId().equals(loggingObject.getContainerObjectId()));
			
			// See if we recognize the repository ID, this is an absolute match
			//
			if (sameCarteFamily && loggingObject.getObjectId()!=null && loggingObject.getObjectId().equals(getObjectId())) {
				return true;
			}
			
		  // If the filename is the same, it's the same object...
			//
			if (sameCarteFamily && !Const.isEmpty(loggingObject.getFilename()) && loggingObject.getFilename().equals(getFilename())) {
				return true;
			}
			
			// See if the carte family, the name & type and parent name & type is the same.
			// This will catch most matches except for the most exceptional use-case.
			//
			if (!sameCarteFamily || 
			    (loggingObject.getObjectName()==null && getObjectName()!=null ) || 
			    (loggingObject.getObjectName()!=null && getObjectName()==null )) {
				return false;
			}
			
			if ( sameCarteFamily && ( (loggingObject.getObjectName()==null && getObjectName()==null ) || 
			       (loggingObject.getObjectName().equals(getObjectName()))) && loggingObject.getObjectType().equals(getObjectType())
			   ) {
				
				// If there are multiple copies of this object, they both need their own channel
				//
				if (!Const.isEmpty(getObjectCopy()) && !getObjectCopy().equals(loggingObject.getObjectCopy())) {
					return false;
				}
				
				LoggingObjectInterface parent1 = loggingObject.getParent();
				LoggingObjectInterface parent2 = getParent();
				
				if ((parent1!=null && parent2==null) || (parent1==null && parent2!=null)) return false;
				if (parent1==null && parent2==null) return true;
				
				// This goes to the parent recursively...
				//
				if (parent1.equals(parent2)) {
					return true;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	private void grabLoggingObjectInformation(LoggingObjectInterface loggingObject) {
		objectType = loggingObject.getObjectType();
		objectName = loggingObject.getObjectName();
		repositoryDirectory = loggingObject.getRepositoryDirectory();
		filename = loggingObject.getFilename();
		objectId = loggingObject.getObjectId();
		objectRevision = loggingObject.getObjectRevision();
		objectCopy = loggingObject.getObjectCopy();
		logLevel = loggingObject.getLogLevel();
		containerObjectId = loggingObject.getContainerObjectId();
		
		if (loggingObject.getParent()!=null) {
			getParentLoggingObject(loggingObject.getParent());
		}
	}

	private void grabObjectInformation(Object object) {
		objectType = LoggingObjectType.GENERAL;
		objectName = object.toString(); // name of class or name of object..
		
		parent = null;
	}

	private void getParentLoggingObject(Object parentObject) {
		
		if (parentObject==null) {
			return;
		}
		
		if (parentObject instanceof LoggingObjectInterface) {
		  parent = (LoggingObjectInterface)parentObject;
		  return;
		}
		
		LoggingRegistry registry = LoggingRegistry.getInstance();
		
		// Extract the hierarchy information from the parentObject...
		//
		LoggingObject check = new LoggingObject(parentObject);
		LoggingObjectInterface loggingObject = registry.findExistingLoggingSource(check);
		if (loggingObject==null) {
			String logChannelId = registry.registerLoggingSource(check);
			loggingObject = check;
			check.setLogChannelId(logChannelId);
		}
		
		parent = loggingObject;
	}
	
	/**
	 * @return the name
	 */
	public String getObjectName() {
		return objectName;
	}
	/**
	 * @param name the name to set
	 */
	public void setObjectName(String name) {
		this.objectName = name;
	}
	/**
	 * @return the repositoryDirectory
	 */
	public RepositoryDirectoryInterface getRepositoryDirectory() {
		return repositoryDirectory;
	}
	/**
	 * @param repositoryDirectory the repositoryDirectory to set
	 */
	public void setRepositoryDirectory(RepositoryDirectory repositoryDirectory) {
		this.repositoryDirectory = repositoryDirectory;
	}
	/**
	 * @return the filename
	 */
	public String getFilename() {
		return filename;
	}
	/**
	 * @param filename the filename to set
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}
	/**
	 * @return the objectId
	 */
	public ObjectId getObjectId() {
		return objectId;
	}
	/**
	 * @param objectId the objectId to set
	 */
	public void setObjectId(ObjectId objectId) {
		this.objectId = objectId;
	}

	/**
	 * @return the objectRevision
	 */
	public ObjectRevision getObjectRevision() {
		return objectRevision;
	}

	/**
	 * @param objectRevision the objectRevision to set
	 */
	public void setObjectRevision(ObjectRevision objectRevision) {
		this.objectRevision = objectRevision;
	}

	/**
	 * @return the id
	 */
	public String getLogChannelId() {
		return logChannelId;
	}

	/**
	 * @param id the id to set
	 */
	public void setLogChannelId(String logChannelId) {
		this.logChannelId = logChannelId;
	}

	/**
	 * @return the parent
	 */
	public LoggingObjectInterface getParent() {
		return parent;
	}

	/**
	 * @param parent the parent to set
	 */
	public void setParent(LoggingObjectInterface parent) {
		this.parent = parent;
	}

	/**
	 * @return the objectType
	 */
	public LoggingObjectType getObjectType() {
		return objectType;
	}

	/**
	 * @param objectType the objectType to set
	 */
	public void setObjectType(LoggingObjectType objectType) {
		this.objectType = objectType;
	}

	/**
	 * @return the copy
	 */
	public String getObjectCopy() {
		return objectCopy;
	}

	/**
	 * @param copy the copy to set
	 */
	public void setObjectCopy(String objectCopy) {
		this.objectCopy = objectCopy;
	}

  public LogLevel getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  /**
   * @return the carteObjectId
   */
  public String getContainerObjectId() {
    return containerObjectId;
  }

  /**
   * @param carteObjectId the carteObjectId to set
   */
  public void setCarteObjectId(String carteObjectId) {
    this.containerObjectId = carteObjectId;
  }

  /**
   * @return the registrationDate
   */
  public Date getRegistrationDate() {
    return registrationDate;
  }

  /**
   * @param registrationDate the registrationDate to set
   */
  public void setRegistrationDate(Date registrationDate) {
    this.registrationDate = registrationDate;
  }
}
