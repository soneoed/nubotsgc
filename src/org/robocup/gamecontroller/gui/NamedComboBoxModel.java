/*
  Copyright (C) 2011 University Of Bremen.
  
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.robocup.gamecontroller.gui;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.MutableComboBoxModel;


/**
 * A ComboBoxModel with named Index
 * @author Thomas Liebschwager
 *
 */
public class NamedComboBoxModel extends AbstractListModel implements MutableComboBoxModel, Serializable{

	Vector<Short> namedIndex; 
	Vector<Object> objects;
	Object selectedObject; 	
	private static final long serialVersionUID = 5197437949930987317L;

	public NamedComboBoxModel() {
		objects = new Vector<Object>();
		namedIndex = new Vector<Short>();
	}

	public NamedComboBoxModel(Object[] items) {
		objects = new Vector<Object>();
		objects.ensureCapacity(items.length);

		int i,c;
		for (i=0,c=items.length;i<c;i++)
			objects.addElement(items[i]);
		if (getSize() > 0) {
			selectedObject = getElementAt(0);
		} 		
			
	}

	public NamedComboBoxModel(Vector<Object> items) {
		objects = items;
		
		if (getSize() > 0) {
			selectedObject = getElementAt(0);
		} 
	}
	
	@Override
	public Object getSelectedItem() {
		return selectedObject;
	}
	
	public Short getSelectedIndex() {
		return namedIndex.elementAt(objects.indexOf(selectedObject));
	}	

	@Override
	public void setSelectedItem(Object anItem) {
		if ((selectedObject != null && !selectedObject.equals(anItem)) || selectedObject == null && anItem != null) {
			selectedObject = anItem;
			fireContentsChanged(this, -1, -1);
		} 	
	}

	@Override
	public Object getElementAt(int index) {
		if (index >= 0 && index < objects.size())
			return objects.elementAt(index);
		else
			return null; 
	}

	@Override
	public int getSize() {
		return objects.size();
	}

	@Override
	public void addElement(Object anObject) {
		objects.addElement(anObject);
		fireIntervalAdded(this,objects.size()-1, objects.size()-1);
		if (objects.size() == 1 && selectedObject == null && anObject != null) {
			setSelectedItem(anObject);
		} 
	}
	
	public void addElement(Object anObject, Short indexName) {
		objects.addElement(anObject);
		namedIndex.addElement(indexName);
		fireIntervalAdded(this,objects.size()-1, objects.size()-1);
		if (objects.size() == 1 && selectedObject == null && anObject != null) {
			setSelectedItem(anObject);
		} 		
	}

	@Override
	public void insertElementAt(Object anObject, int index) {
		objects.insertElementAt(anObject, index);
		fireIntervalAdded(this, index, index); 
	}

	@Override
	public void removeElement(Object anObject) {
		int index = objects.indexOf(anObject);
		if (index != -1) {
			removeElementAt(index);
		} 
	}

	@Override
	public void removeElementAt(int index) {
		if(getElementAt(index) == selectedObject) {
			if (index == 0) {
				setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
			}
			else {
				setSelectedItem(getElementAt(index - 1));
			}
		}
		objects.removeElementAt(index);
		fireIntervalRemoved(this, index, index); 
	} 

}


