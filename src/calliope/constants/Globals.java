/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calliope.constants;

/**
 * Configurable global settings
 * @author desmond
 */
public class Globals 
{
    /** controls local/external fetching of jquery */
    public static final String JQUERY_SITE = "code.jquery.com";
    //public static final String JQUERY_SITE = "localhost";  
    /** disables import */
    public static final boolean DEMO = true;
    /** maximum size of an uploaded file - don't increase unless you have to */
    public static int MAX_UPLOAD_LEN = 64000;
    /** default dictionary */
    public static String DEFAULT_DICT = "en_GB";
}
