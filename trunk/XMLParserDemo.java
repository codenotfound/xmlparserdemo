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
                System.out.print("Parser started. Enter commands.Possible commands: ");
                
                String s="";
                for (Map.Entry<String, Command> entry : ui.comms.entrySet())
                {
					s+=entry.getKey()+", ";// + " - " + entry.getValue().desc +"\n";
				}
				s+="\n";
                System.out.print(s);
                System.out.println("Type \"help\" to get description of commands");
                
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
                Map<String, Command> comms;
                File xmlFile;
                String [] cfgComms;
                
                public UI(){
                        in = new BufferedReader ( new InputStreamReader(System.in) );
                        out = new PrintWriter(System.out);
                        
                        comms = new CommandMap<String, Command>();
                        ParserCommand pc = new ParserCommand(){
                                public void execute(UI ui)
                                {
                                        System.out.println(ui.eng.doc.getDocumentElement().getTagName());
                                         
                                }
                        };
                        comms.put("docName", new Command("docName",pc,"docName Description", this));
                        
                        pc = new ParserCommand()
                        {
							public void execute(UI ui)
								{
									
											 
								}
                        };
                        comms.put("help", new Command("help",pc,"help Description", this));
                        
                        pc = new ParserCommand()
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
                        };
                        comms.put("docStruct", new Command("docStruct",pc,"docStruct Description", this));
                        
                        pc = new ParserCommand()
                        {
							public void execute(UI ui)
							{
								System.exit(0);
                                         
							}
						};
						comms.put("exit", new Command("exit",pc,"exit Description", this));
                        
                        
                }
        
        
                public void setEngine(Engine link)
                {
                        eng = link;
                }
                
                public int parseCfg(String s)//0=only file 1 2 ... =file+comms -1=error
                {
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
                                        System.out.print("Enter file name and arguments. Possible arguments: ");
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
                                        
                                        
                                        
                                        while(true)
                                        {
                                                getCommand(comms).execute(this);
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
				
			public ParserCommand getCommand(FilterInput m)
			{
				String s="";
				while(true)
				{
					try
					{
						s = in.readLine();
					}
					catch(IOException ioe)
					{
						ioe.printStackTrace();
					}
					if(m.isOk(s))
					{
						break;
					}
					else
					{
					System.out.println("Command not found, type \"help\" to get list of possible commands");
					}
				}
				return m.getCommand(s).commandCode;
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
                                                s+=entry.getKey()+", "; //+ " - " + entry.getValue() +"\n";
                                        }
                                        return s+"\n";
                                }
                                
        }
class Command 
{
	String name;
	ParserCommand commandCode;
    String desc;
    UI ui;
    
    Command(String nm, ParserCommand cc, UI uint)
    {
		name = nm;
		commandCode =cc;
		desc = "";
		ui = uint;
    }
    Command(String nm, ParserCommand cc, String d, UI uint)
    {
		name = nm;
		commandCode =cc;
		desc = d;
		ui = uint;
    }
    public void execute()
    {
		commandCode.execute(ui);
    }     
}
class CommandMap<String, Command> extends HashMap<String, Command> implements FilterInput
{
	public boolean isOk(java.lang.String s)
	{
		return this.containsKey(s);
	}
	public java.lang.String getHelp()
	{
		return "Help";
	}
	public Command getCommand(java.lang.String s)
	{
		//Command tmp = ((Command) this.get(s));
		return this.get(s);
	}
}
interface ParserCommand
{
	public void execute(UI ui);     
}
interface FilterInput
{
	public boolean isOk(String s);
	public String getHelp();
	public Command getCommand(String s);
	
}