/* This file is part of calliope.
 *
 *  calliope is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  calliope is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with calliope.  If not, see <http://www.gnu.org/licenses/>.
 */

package calliope.importer.filters;
import calliope.importer.Archive;
import calliope.json.JSONDocument;
import calliope.exception.ImportException;
/**
 * Import a poem with stanzas and a title
 * @author desmond
 */
public class PoemFilter extends Filter
{
    MarkupSet markup;
    
    public PoemFilter()
    {
        super();
    }
    @Override
    public void configure( JSONDocument config )
    {
        System.out.println("Poem filter: config");
    }
    /**
     * Return something for a dropdown menu
     * @return a String
     */
    @Override
    public String getDescription()
    {
        return "Poem with stanzas";
    }
    /**
     * Convert to standoff properties
     * @param input the raw text input string
     * @param name the name of the new version
     * @param cortext a cortext mvd archive
     * @param corcode a corcode mvd archive
     * @return log output
     */
    @Override
    public String convert( String input, String name, Archive cortex, 
        Archive corcode ) throws ImportException
    {
        StringBuilder txt = new StringBuilder();
        String[] lines = input.split("\n");
        String localTitle="";
        String tempTitle="";
        int lgStart = 0;
        markup = new MarkupSet();
        int state = 0;
        for ( int i=0;i<lines.length;i++ )
        {
            lines[i] = lines[i].trim();
            switch ( state )
            {
                case 0:
                    if ( lines[i].length()>0 )
                    {
                        tempTitle = lines[i];
                        state = 1;
                    }
                    break;
                case 1:
                    if ( lines[i].length()==0 )
                    {
                        localTitle = tempTitle;
                        //txt.append("<head>");
                        markup.add("head",0,localTitle.length());
                        txt.append( localTitle );
                        txt.append( "\n" );
                        lgStart = localTitle.length()+1;
                        //xml.append("</head>\n");
                        //xml.append( "<lg>" );
                    }
                    else    // no title
                    {
                        //xml.append( "<lg><l>" );
                        lgStart = txt.length();
                        if ( localTitle.length()==0 && tempTitle.length()>0 )
                        {
                            markup.add("l",txt.length(),tempTitle.length());
                            txt.append( tempTitle );
                        }
                        //xml.append( "</l>\n");
                        //xml.append( "<l>");
                        markup.add("l",txt.length(),lines[i].length());
                        txt.append( lines[i] );
                        txt.append("\n");
                        //xml.append( "</l>\n" );
                    }
                    state = 2;
                    break;
                case 2:
                    if ( lines[i].length()>0 )
                    {
                        markup.add( "l",txt.length(),lines[i].length() );
                        txt.append( lines[i] );
                        txt.append( "\n" );
                    }
                    else
                    {
                        markup.add("lg",lgStart,txt.length()-lgStart);
                        txt.append("\n");
                        state = 3;
                    }
                    break;
                case 3: // new stanza
                    if ( lines[i].length()>0 )
                    {
                        lgStart = txt.length();
                        markup.add( "l",txt.length(),lines[i].length() );
                        txt.append( lines[i] );
                        txt.append( "\n" );
                        state = 2;
                    }
                    break;
            }
        }
        if ( state == 2 )
        {
            markup.add("lg",lgStart,txt.length()-lgStart);
            txt.append("\n");
        }
        markup.sort();
        cortex.put( name, txt.toString().getBytes() );
        corcode.put( name, markup.toSTILDocument().toString().getBytes() );
        return "";
    }
    /**
     * Do the perl chomp: remove trailing whitespace
     * @param sb the StringBuilder that's for chomping
     */
    private void chomp( StringBuilder sb )
    {
        while ( sb.length()>0 
            && Character.isWhitespace(sb.charAt(sb.length()-1)))
            sb.setLength( sb.length()-1 );
    }
}
