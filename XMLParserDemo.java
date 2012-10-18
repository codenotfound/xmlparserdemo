import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.*;
import java.nio.*;
import java.util.*;

class XMLParserDemo
{ //klass-obolochka
    XMLParserDemo()
	{
        Engine eng = new Engine();
        UI ui = new UI();
        eng.setUI(ui);
        ui.setEngine(eng);
        eng.start();
        ui.start();     
    }
        
    public static void main(String [] args)
	{
        XMLParserDemo p = new XMLParserDemo();
    }
        
        
}
class Engine extends Thread
{
    UI ui = null;    
    InputStream XMLTextSource = null;
    Document doc = null;
    DocumentBuilder db;
    Cfg configs = null;
                
    Engine ()
	{                        
        try
		{
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            db = dbf.newDocumentBuilder(); 
        }
        catch(ParserConfigurationException pce)
		{
			System.out.println("Engine ParserConfigurationException");
			pce.printStackTrace();
        }       
    }
                
    public void setUI(UI link)
    {
		ui = link;
    }
    public void run()
    {
        if(ui==null)
        {
            System.out.println("NO UI");
            System.exit(0);                                
        }
                        
                        
        while(true)
        {
            try
			{
                while(true)
                {
                    sleep(216000000);
                }
            }
            catch (InterruptedException e)
            {
                System.out.println("Parser started. Enter commands. Possible commands: docName, docStruct.");
                try
				{
                    if(XMLTextSource.available() < 1)
					{
                        doc = db.newDocument();
                    } 
                    else
                    {                                                
						try
						{
							doc = db.parse(XMLTextSource);
							interrupted();
							//System.out.println("Parser finished");
							//System.out.println("doc="+doc);
						}       
						catch (IOException ioe)
						{
							System.out.println("Engine IOException");
						}       
						catch (org.xml.sax.SAXException se)
						{
							System.out.println("Engine SAXException");
						}       
					}
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace();
				}
			}                                
		}                 
	}
} 
        
        class UI extends Thread{
                Engine eng = null;
                BufferedReader in;
                PrintWriter out;
                Map<String, ParserCommand> comms;
                File xmlFile;
                String [] cfgComms;
                
                public UI(){
                        in = new BufferedReader (new InputStreamReader(System.in) );
                        out = new PrintWriter(System.out);
                        
                        comms = new HashMap<String, ParserCommand>();
                        comms.put("docName",new ParserCommand()
                        {
                                public void execute(UI ui)
                                {
                                        System.out.println(ui.eng.doc.getDocumentElement().getTagName());
                                         
                                }
                        }); 
                        
                        comms.put("docStruct",new ParserCommand()
                        {
                                public void execute(UI ui)
                                {
                                        //System.out.println(ui.eng.doc.getDocumentElement().getTagName());
                                        
                                        treeParse(ui.eng.doc.getDocumentElement(),0);
                                         
                                }
                                public void treeParse(Node nd, int tab)
                                {
                                        String space = "";
                                        for(int i = 0; i<tab; i++)
                                                space+="  ";
                                                
                                        tab++;
                                        
                                        System.out.print( space + nd.getNodeName());
                                        
                                        if(eng.configs.isSet("va")==true)
                                        {                                       
                                                for(int i=0; i<nd.getAttributes().getLength(); i++)
                                                {
                                                System.out.print(" "+nd.getAttributes().item(i).getNodeName()+"= \""
                                                +nd.getAttributes().item(i).getNodeValue()+"\"");
                                                }
                                        }
                                        
                                        System.out.println();
                                        
                                        for(int i=0; i<nd.getChildNodes().getLength(); i++)
                                        {
                                                if(nd.getChildNodes().item(i).getNodeName()=="#text"&&eng.configs.isSet("tc")==true&&nd.getChildNodes().item(i).getNodeValue().trim().length()!=0){
                                                                                                        System.out.println(space+"\""+nd.getChildNodes().item(i).getNodeValue()+"\"");
                                                                                                        //System.out.println("before :'"+nd.getChildNodes().item(i).getNodeValue()+"' after :'"+nd.getChildNodes().item(i).getNodeValue().trim()+"'");
                                                }  //?????
                                                 
                                                if(nd.getChildNodes().item(i).getNodeName()!="#text")
                                                        treeParse(nd.getChildNodes().item(i), tab);
                                        }
                                        
                                                                        
                                }
                                
                                
                        }); 
                        
                        
                }
        
        
                public void setEngine(Engine link)
                {
                        eng = link;
                }
                
                public int parseCfg(String s)//0=only file 1 2 ... =file+comms -1=error
                {
                        
                        //
                        
                        File tmpfile = new File (s);
                        if(tmpfile.isFile())
                        {       
                                xmlFile = tmpfile;
                                cfgComms = new String[0];
                                return 0;
                        }       
                        else
                        {
                                String[] m  = s.trim().split(".xml");
                                
                        //      System.out.println("m length "+m.length);

                                String tmpstr = "";
                                for(int i = 0; i<m.length-1;i++)
                                {
                                        tmpstr+=m[i]+".xml";
                                }
                                //System.out.println("tmpstr "+tmpstr);
                                tmpfile = new File (tmpstr);
                                if(tmpfile.isFile())
                                {
                                        xmlFile = tmpfile;
                                        
                                        cfgComms = m[m.length-1].split(" -");
                                        
                                                                                List<String> list = Arrays.asList(cfgComms);
                                                                                list = list.subList(1,list.size());
                                                                                cfgComms = new String[0];
                                                                                cfgComms = list.toArray(cfgComms);
                                        Arrays.sort(cfgComms);
                                                                                /*
                                                                                //System.arraycopy(arguments, 1, arguments,0, arguments. length -1);
                                        System.out.println("ARGUMENTS: ");
                                       // System.out.println(" " + s.substring(tmpstr.length()).trim());
                                        for(int i = 0; i<cfgComms.length;i++)
                                        {
                                            System.out.println(i +" "+cfgComms[i]);
                                        }
                                        */
                                        //cfgComms = new String[1];
                                        //cfgComms[0] = m[m.length-1];                                  
                                        return cfgComms.length;
                                }
                                else
                                {
                                        System.out.println("Incorrect file name");
                                        return -1;

                                }
                                
                                                                
                        }
                        
                        /*
                        xmlFile = new File (m[0]);
                        if( m.length ==1)
                                return 0;
                        cfgComms = new String[1];       
                        cfgComms[0] = m[1];
                        return 1;
                        */
                }
                
                public void run()
                {
                        if(eng==null)
                        {
                                System.out.println("NO ENG");
                                System.exit(0);
                                
                        }
                        else
                        {
                                try{
                                                                                eng.configs = new Cfg();    
                                        System.out.println("Enter file name and arguments. Possible arguments: ");
                                                                                System.out.print(eng.configs);
                                        System.out.println("Note: case sensitive!");
                                        String s = in.readLine();
                                        
                                                                                while(parseCfg(s)<0)
                                        {       
                                         System.out.println("File not found");    
                                         s = in.readLine();   
                                                //eng.configs = new Cfg(S.split(" -"))
                                        
                                        }
                                        eng.configs.actualize(cfgComms);
                                                                                
                                        eng.XMLTextSource = new FileInputStream(xmlFile);
                                        eng.interrupt();
                                        //System.out.println(eng.getState().name().compareTo("TIMED_WAITING"));
                                        while(eng.isInterrupted()){
                                                Thread.sleep(1000);
                                        }
                                        
                                        String comm = "";
                                        
                                        while(true)
                                        {
                                                comm = in.readLine();
                                                if(comms.containsKey(comm))
                                                {
                                                        comms.get(comm).execute(this);
                                                }
                                                else
                                                {
                                                        System.out.println("Command not found");
                                                }
                                                
                                                
                                        }
                                        //System.out.println(eng.doc);
                                        //System.exit(0);
                                }
                                catch(Exception e)
                                {
                                        System.out.println("UI Exception");
                                        e.printStackTrace();
                                }
                                                        
                        }
                }
        }
        
        class Cfg
        {
            
        private NavigableMap<String, String> commands;
                Cfg()
                {
                                        commands = new TreeMap<String, String>();
                                        commands.put("va","viewAttributes");
                                        commands.put("tc","textContent");
                
                }
                                void actualize(String[] argarray)
                                {
                                        if (argarray.length==0)
                                        {       
                                                commands.clear();
                                                return;
                                        }
                                        
                                        for (int i = 0;i<argarray.length;i++)
                                        {
                                                while(argarray[i].compareTo(commands.floorKey(argarray[i]))!=0)
                                                        commands.remove(commands.floorKey(argarray[i]));
                                        }
                                        while(commands.higherKey(argarray[argarray.length-1])!=null)
                                                commands.remove(commands.higherKey(argarray[argarray.length-1]));
                                }
                                public boolean isSet(String key)
                                {
                                        return commands.containsKey(key);
                                }
                                public String toString()
                                {
                                        String s = "";
                                        
                                        for (Map.Entry<String, String> entry : commands.entrySet())
                                        {
                                                s+=entry.getKey() + " - " + entry.getValue() +"\n";
                                        }
                                        return s;
                                }
                                
        }
        
        interface ParserCommand{
                public void execute(UI ui);     
        }