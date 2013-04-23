/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calliope.tests.html;

import calliope.Utils;
import calliope.constants.HTMLNames;
import calliope.constants.JSONKeys;
import calliope.json.JSONDocument;
import java.util.ArrayList;
import java.util.Set;
import java.util.Iterator;

/**
 * Generate a select element from an _all_docs query result from couchdb
 * @author desmond
 */
public class HTMLDocSelect extends Element
{
    /**
     * Convert an _all_docs output from couchdb to a select list
     * @param json the text of the _all_docs call
     * @param name the name of the select list
     * @param id the id of the select
     */
    public HTMLDocSelect( String json, String name, String id )
    {
        super( HTMLNames.SELECT );
        if ( id != null )
            this.addAttribute(HTMLNames.ID, id );
        if ( name != null )
            this.addAttribute( HTMLNames.NAME, name );
        load( json );
    }
    private String makeOptLabel( String[] parts )
    {
        StringBuilder sb = new StringBuilder();
        for ( int i=0;i<parts.length-1;i++ )
        {
            if ( sb.length()>0 )
                sb.append("-");
            sb.append( parts[i] );
        }
        return sb.toString();
    }
    /**
     * Load a json document being the output of _all_docs
     * @param json the all docs response as a string
     */
    private void load( String json )
    {
        JSONDocument doc = JSONDocument.internalise( json );
        if ( doc != null )
        {
            ArrayList docs = (ArrayList) doc.get( JSONKeys.ROWS );
            if ( docs != null )
            {
                for ( int i=0;i<docs.size();i++ )
                {
                    JSONDocument d = (JSONDocument)docs.get(i);
                    String key = (String) d.get( JSONKeys.KEY );
                    String[] parts = key.split("/");
                    if ( parts.length == 1 )
                    {
                        addChild( new HTMLOption(key,parts[0]) );
                    }
                    else if ( parts.length > 1 )
                    {
                        HTMLOptGroup g = null;
                        String optLabel = makeOptLabel( parts );
                        try
                        {
                            for ( int j=0;j<this.numChildren();j++ )
                            {
                                Element e = this.getChild( j );
                                if ( e instanceof HTMLOptGroup )
                                {
                                    HTMLOptGroup group = (HTMLOptGroup)e;
                                    String label = group.getAttribute(
                                        HTMLNames.LABEL);
                                    if ( label != null 
                                        && label.equals(optLabel) )
                                    {
                                        g = group;
                                        break;
                                    }
                                }
                            }
                        }
                        catch ( Exception e )
                        {
                            System.out.println(e.getMessage());
                        }
                        if ( g == null )
                        {
                            g = new HTMLOptGroup( optLabel );
                            addChild( g );
                        }
                        g.addChild( new HTMLOption(key,parts[parts.length-1]) );
                    }
                }
            }
        }
    }
}
