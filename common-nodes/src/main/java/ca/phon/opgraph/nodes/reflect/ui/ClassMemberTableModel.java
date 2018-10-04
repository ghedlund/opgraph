/*
 * Copyright (C) 2012-2018 Gregory Hedlund <https://www.phon.ca>
 * Copyright (C) 2012 Jason Gedge <http://www.gedge.ca>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ca.phon.opgraph.nodes.reflect.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Table model for {@link Class} {@link Member}s.  Includes
 * options for filtering the type of {@link Member}s displayed.
 */
public class ClassMemberTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -7387988514024686010L;

	private final Class<?> declaredClass;
	
	/*
	 * Options
	 */
	private boolean showConstructors = true;
	
	private boolean showStaticFields = true;
	
	private boolean showStaticMethods = true;
	
	private boolean showNonStaticFields = true;
	
	private boolean showNonStaticMethods = true;
	
	private final List<Member> memberList = 
			Collections.synchronizedList(new ArrayList<Member>());
	
	public ClassMemberTableModel(Class<?> declaredClass) {
		super();
		this.declaredClass = declaredClass;
		scanClass();
	}

	public Class<?> getDeclaredClass() {
		return this.declaredClass;
	}
	
	public boolean isShowConstructors() {
		return showConstructors;
	}

	public void setShowConstructors(boolean showConstructors) {
		this.showConstructors = showConstructors;
		scanClass();
	}

	public boolean isShowStaticFields() {
		return showStaticFields;
	}

	public void setShowStaticFields(boolean showStaticFields) {
		this.showStaticFields = showStaticFields;
		scanClass();
	}

	public boolean isShowStaticMethods() {
		return showStaticMethods;
	}

	public void setShowStaticMethods(boolean showStaticMethods) {
		this.showStaticMethods = showStaticMethods;
		scanClass();
	}

	public boolean isShowNonStaticMethods() {
		return showNonStaticMethods;
	}

	public void setShowNonStaticMethods(boolean showNonStaticMethods) {
		this.showNonStaticMethods = showNonStaticMethods;
		scanClass();
	}

	public boolean isShowNonStaticFields() {
		return showNonStaticFields;
	}

	public void setShowNonStaticFields(boolean showNonStaticField) {
		this.showNonStaticFields = showNonStaticField;
		scanClass();
	}

	private void scanClass() {
		memberList.clear();
		if(isShowConstructors()) {
			for(Constructor<?> cstr:getDeclaredClass().getConstructors()) {
				memberList.add(cstr);
			}
		}
		
		if(isShowStaticFields()) {
			for(Field field:getDeclaredClass().getFields()) {
				if(Modifier.isStatic(field.getModifiers())) 
					memberList.add(field);
			}
		}
		
		if(isShowNonStaticFields()) {
			for(Field field:getDeclaredClass().getFields()) {
				if(!Modifier.isStatic(field.getModifiers())) 
					memberList.add(field);
			}
		}
		
		if(isShowStaticMethods()) {
			for(Method method:getDeclaredClass().getMethods()) {
				if(Modifier.isStatic(method.getModifiers())) {
					memberList.add(method);
				}
			}
		}
		
		if(isShowNonStaticMethods()) {
			for(Method method:getDeclaredClass().getMethods()) {
				if(!Modifier.isStatic(method.getModifiers())) {
					memberList.add(method);
				}
			}
		}
		super.fireTableDataChanged();
	}
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		return memberList.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
