package com.terry.interview.emc;

import com.google.common.base.StandardSystemProperty;
import com.google.common.io.Resources;
import com.terry.interview.emc.annotation.ResFile;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.collections.Sets;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

/**
 * Created by Xianghe on 2017/4/14.
 */
public class TestBase {
    @BeforeClass(alwaysRun = true)
    protected void setupAnnotatedFiles(ITestContext ctx) {

        Set<Field> resourceFileFields = getAnnotatedFields(this.getClass(),
                ResFile.class);


		/*
		 * Locate and populate any resource directories
		 */

        if (!resourceFileFields.isEmpty()) {

            for (Field field : resourceFileFields) {

				/*
				 * Validate that the data type is correct
				 */

                checkArgument(
                        field.getType().equals(File.class),
                        format("The field <%s>, which was annotated with @ResourceFile, should be of type File",
                                field.getName()));

				/*
				 * Create the object and confirm that ii exists and is readable.
				 */

                ResFile annotation = field.getAnnotation(ResFile.class);

                URL resourceUrl = Resources.getResource(annotation.value());
                File resourceFile = new File(resourceUrl.getFile());

                checkArgument(
                        resourceFile.exists(),
                        format("The field <%s>, which was annotated with @ResourceFile, points to location <%s> which does not exist.",
                                field.getName(), annotation.value()));

                checkArgument(
                        resourceFile.isFile(),
                        format("The field <%s>, which was annotated with @ResourceFile, points to location <%s> which appears to be a file not  a directory.",
                                field.getName(), annotation.value()));

                setFieldValue(this, field, resourceFile);
            }
        }
    }

    protected void setFieldValue(Object obj, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(obj, value);
        } catch (SecurityException e) {
            throw new IllegalStateException(
                    format("A security exception was thrown when we attempted to set the contents of field <%s> of the <%s> class to the value <%s>",
                            field.getName(), obj.getClass().getName(), value),
                    e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException(
                    format("A argument exception was thrown when we attempted to set the contents of field <%s> of the <%s> class to the value <%s>. This exception usually means that the programmer used the wrong types and they cannot be converted.",
                            field.getName(), obj.getClass().getName(), value),
                    e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                    format("A access exception was thrown when we attempted to set the contents of field <%s> of the <%s> class to the value <%s>",
                            field.getName(), obj.getClass().getName(), value),
                    e);
        }
    }


    /**
     * Get a list of fields that have been annotated with the given annotation
     * class
     *
     * @param clazz
     * @return
     */
    protected Set<Field> getAnnotatedFields(Class<? extends Object> clazz,
                                            Class<? extends Annotation> annotationClazz) {
        Set<Field> annotatedFields = Sets.newHashSet();

		/*
		 * As long as the class we are passing in is not the object class we
		 * will keep walking up the hierarchy
		 */

        if (Object.class.equals(clazz)) {
            return annotatedFields;
        }

		/*
		 * Add the declared fields that have the annotations
		 */

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {

            if (field.isAnnotationPresent(annotationClazz)) {
                annotatedFields.add(field);
            }

        }

		/*
		 * Walk up through the parent classes
		 */

        annotatedFields.addAll(getAnnotatedFields(clazz.getSuperclass(),
                annotationClazz));

        return annotatedFields;
    }

}
