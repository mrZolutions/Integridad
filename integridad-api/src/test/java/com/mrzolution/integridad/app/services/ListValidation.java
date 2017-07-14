package com.mrzolution.integridad.app.services;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;

import javax.persistence.ManyToOne;

import org.junit.Assert;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ListValidation {

	public static void checkListsAndFatherNull(Class<?> classToGetField, Object object)
			throws IllegalAccessException {
		log.info("checkListsAndFatherNull class: {}", classToGetField);
		Field[] fieldsGrandChild =  classToGetField.getDeclaredFields();
		for (int i = 0; i < fieldsGrandChild.length; i++) {
			fieldsGrandChild[i].setAccessible(true);
			if(Collection.class.isAssignableFrom(fieldsGrandChild[i].getType())){
				log.info("assertnull LIST: {}", fieldsGrandChild[i]);
				Assert.assertNull(fieldsGrandChild[i].get(object));
			}

			if(fieldsGrandChild[i].getAnnotation(ManyToOne.class) != null){
				Field[] fieldsFather =  fieldsGrandChild[i].getType().getDeclaredFields();
				for (int j = 0; j < fieldsFather.length; j++) {
					fieldsFather[j].setAccessible(true);
					if(Collection.class.isAssignableFrom(fieldsFather[j].getType())){
						log.info("father assertnull 0: {}", fieldsFather[j]);
						log.info("father assertnull 1: {}", fieldsGrandChild[i]);
						log.info("father assertnull 2: {}", fieldsFather[j].get(fieldsGrandChild[i].get(object)));
						Assert.assertNull(fieldsFather[j].get(fieldsGrandChild[i].get(object)));
					}
				}
			}
		}
	}

	public static void childsLisAndFathertValidation(Class<?> classToGetFields, Object response) throws IllegalAccessException, ClassNotFoundException,
			NoSuchMethodException, InstantiationException, InvocationTargetException {

		log.info("childsListAndFathertValidation class: {}", classToGetFields);
		Field[] fields = classToGetFields.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);
			if(Collection.class.isAssignableFrom(fields[i].getType())){
				log.info("assertnotnull LIST: {}", fields[i]);
				Assert.assertNotNull(fields[i].get(response));

				//Getting fields from list Type in order to verify sublists null
				List<Class<?>> lista = (List<Class<?>>) fields[i].get(response);

				String genericType = fields[i].getGenericType().toString();
				String requiredClass = genericType.substring(genericType.indexOf("<") + 1, genericType.indexOf(">"));
				Class<?> c = Class.forName(requiredClass);
				Constructor<?> cons = c.getConstructor();
				Object object = cons.newInstance();

				Field[] fieldsChild =  object.getClass().getDeclaredFields();
				for(int k=0; k<lista.size(); k++){
					for (int j = 0; j < fieldsChild.length; j++) {
						fieldsChild[j].setAccessible(true);
						if(Collection.class.isAssignableFrom(fieldsChild[j].getType())){
							log.info("check SUBLIST null: {}", fieldsChild[j]);
							Assert.assertNull(fieldsChild[j].get(lista.get(k)));
						} else if (!classToGetFields.equals(fieldsChild[j].getType())){
							log.info("checkListsAndFatherNull from SUBLIST field: {}", fieldsChild[j]);
							checkListsAndFatherNull(fieldsChild[j].getType(), fieldsChild[j].get(lista.get(k)));
						}
					}
				}
			}

			if(fields[i].getAnnotation(ManyToOne.class) != null){
				log.info("verifying FATHER: {}", fields[i]);
				Field[] fieldsFather =  fields[i].getType().getDeclaredFields();
				for (int j = 0; j < fieldsFather.length; j++) {
					fieldsFather[j].setAccessible(true);
					log.info("checkListsAndFatherNull from FATHER field: {}", fieldsFather[j]);
					checkListsAndFatherNull(fieldsFather[j].getType(), fieldsFather[j].get(fields[i].get(response)));
				}
			}
		}
	}
}
