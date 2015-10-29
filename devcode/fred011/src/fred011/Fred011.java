
package fred011;

import com.xerox.docushare.*;
import com.xerox.docushare.object.*;
import com.xerox.docushare.property.*;
import com.xerox.docushare.query.*;
import java.io.File;

/**
 *
 * @author MICHAEL
 */
public class Fred011 {
        
    public DSSession dsSession;
    public String sHost;
    public String sDomain;
    public String sUsername; 
    public String sPassword;
    public String sSourceCollTitle;
    public String sNewCollTitle;
    public String sDocTitle;    
    public static String sCollClassName = "Collection";
    public static String sDocClassName = "Document";
    public static String sTitlePropName = "title";
    public String sTargetCollTitle;
    public String sNewTargetCollTitle;
    public String sSrcFile;
    public static int nOption;
    public int nOption2;
    public String sOption="0";    
    public int nNullDocTitle;
    public String sNullDocTitle;
    DSObject[] dsNewCollObjs;
    DSObject[] dsDocObjs;
    
        
    public Fred011() {
        nOption = 0;
        nOption2 = 0;        
    }   
    
    DSObject[] getDsObjFromTitle(String sObjTitle, String sClassName, String[] sCollScopeHandles) throws Exception {
        DSCollectionScope dsCollScope;
        try {
            
            if(sObjTitle == null) {
                throw new Exception("[getDsObjFromTitle] sObjTitle: NULL");
            }
            
            if(sClassName == null) {
                throw new Exception("[getDsObjFromTitle] sClassName: NULL");
            }
            
            if(sCollScopeHandles != null) {
                DSHandle[] dsCollScopeHandles = new DSHandle[sCollScopeHandles.length];
                for(int i=0; i<sCollScopeHandles.length; i++) {
                    dsCollScopeHandles[i] = new DSHandle(sCollScopeHandles[i]);
                }

                dsCollScope = new DSCollectionScope(dsCollScopeHandles);
            }
            else {
                dsCollScope = null;
            }
            
            //Build query
            System.out.println("qeuryExp: match(" + sTitlePropName + ", " + sObjTitle + ")");
            DSQueryExp qeuryExp = DSQuery.matches(sTitlePropName,sObjTitle);
            System.out.println("build query");
            
            //code for searching
            DSQuery query = new DSQuery(qeuryExp, null, dsCollScope, new String[] {sClassName}, null);            
            System.out.println("search query");
            DSSearchResultSet resultSet = dsSession.search(query);
            System.out.println("get iterator");
            DSResultIterator iter = resultSet.iterator();
            
            //code for getting the number of results
            System.out.println("get nResults");
            int nResults = iter.getTotalCount();
            System.out.println("Found: " + nResults + " Results");
            if(nResults == 0) {
                System.out.println("getDsObjectFromTitle: return null");
                return null;
            }
            
            DSObject[] dsObjs = new DSObject[nResults];
            
            for(int i=0; i<nResults; i++) {
                System.out.println("i:" + i);
                System.out.println("get nextObject");
                DSResultItem dsResItm = iter.nextObject();
                 System.out.println("get Object");
                dsObjs[i] = dsResItm.getObject();
            }
            
            return dsObjs;
            
        }
        catch(Exception E) {
            throw new Exception("getDsObject(" + sObjTitle + "): "+ E.getMessage());
        }
    }
 
    public void createColl() throws Exception {
        DSCollection dsSourceColl = null;
        
        try {        
            
            System.out.println("\n\nCREATE COLLECTION\n");            
            sSourceCollTitle = System.console().readLine("\nSource Collection Title: ");
                  
            System.out.println("\nSearching for Collection with Title: " + sSourceCollTitle);
            DSObject[] dsSourceCollObjs = getDsObjFromTitle(sSourceCollTitle,sCollClassName,null);
            System.out.println("\nSearch complete");
            
            //Did not find the source collection searched for
            if(dsSourceCollObjs == null ) {                           
                
                System.out.println("Main Source Collection Not Found In The Search");
                System.out.println("");
                System.out.println("\n\nCollection: " + "[" + sSourceCollTitle + " ]" + " Was Not Found\n" );
                //The sub menu 
                System.out.println("1. Enter New Source Collection Title  \n");
                System.out.println("2. Return to Main Menu  \n");
                System.out.println("3. Exit The Application");
                
                sOption = System.console().readLine("Enter Option: ");
                                
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                               
                switch(nOption) {
                    case 1:
                        System.out.println("Processing Option 1");
                        this.createColl();
                        break;
                    case 2:
                        System.out.println("Processing Option 2");
                        this.getOptionsFromCmd();
                        break;
                    //freds test to exit the application within the method create collection  dsSourceCollObjs == null  
                    case 3:
                        System.out.println("Processing Option 3");
                        System.out.println("");
                        this.finalize();
                        break;
                    default:
                        System.out.println("Invalid Option"+","+"Choose between option 1 - 3");                        
                        this.getOptionsFromCmd();
                        
                        break;                
                }//switch(nOption)  
            }//if(dsSourceCollObjs.length == 0)
            
            //Multiple source collections found with given title
            if (dsSourceCollObjs.length > 1) {
                
                //loop through dsObjs array and construct a ordered numbered sequence of options for the user to choose from.
                //dont forget to append n+1 and n+2
                
                //Inform user multiple collections foind with title: <var>
                System.out.println("\n\nMULTIPLE COLLECTIONS FOUND WITH TITLE: " + sSourceCollTitle + "\n" );
                
                //we are going to display the list of collections with result
                for (int i = 0; i < dsSourceCollObjs.length; i++) {
                    System.out.println("\n" + (i + 1 ) + ". " + dsSourceCollObjs[i].getTitle() + "(" + dsSourceCollObjs[i].getHandle().toString() + ")" + dsSourceCollObjs[i].getOwner().toString());
                }
                
                System.out.println("\n" + (dsSourceCollObjs.length + 1 ) +  ". Enter Source Collection Title Again");
                System.out.println("\n" + (dsSourceCollObjs.length + 2 ) +  ". Return to Main Menu");
                
                sOption = System.console().readLine("\nOption: ");
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                
                //One of the multiple locations has been selected by the user
                
                if(nOption >= 1 && nOption <= dsSourceCollObjs.length) {
                    dsSourceColl = (DSCollection) dsSourceCollObjs[nOption-1];
                    
                }
                
                else {
                    //Option Selected for Enter Source Collection Title Again
                    if(nOption == dsSourceCollObjs.length + 1) {         
                        this.createColl();
                    }
                    else {
                        //Option Selected to Return to Main Menu
                        if(nOption == dsSourceCollObjs.length + 2) {
                            this.getOptionsFromCmd();
                        }
                        else {                            
                            System.out.println(nOption + " is an Invalid Option");
                        }// if(nOption == dsSourceCollObjs.length + 1)
                    }// if(nOption == dsdsSourceCollObjsObjs.length + 1)
                }// if(nOption >= 1 and nOption <= dsSourceCollObjs.length)
            }//if (dsSourceCollObjs.length > 1)               
            
            
            //Single source collections found with given title
            if (dsSourceCollObjs.length == 1) {
                //Inform user single collections found with title: <var>
                System.out.println("\nSINGLE COLLECTION FOUND WITH TITLE: " + sSourceCollTitle + "\n" );
                dsSourceColl = (DSCollection) dsSourceCollObjs[0];
                            
                //we are going to display the list of collections with result
                for (int i = 0; i < dsSourceCollObjs.length; i++) {
                    System.out.println("\n" + (i + 1 ) + ". " + dsSourceCollObjs[i].getTitle() + "(" + dsSourceCollObjs[i].getHandle().toString() + ")"   +"("+  dsSourceCollObjs[i].getOwner().toString()+")");
                                       
                }
            }//if (dsSourceCollObjs.length == 1)   
            
            sNewCollTitle = System.console().readLine("\nEnter New Collection Title: ");
            System.out.println("");
            System.out.println("");
            
            System.out.println("[getDsObjFromTitle(" + sNewCollTitle + ", " + sCollClassName + ", " + dsSourceColl.getHandle().toString() +")]");            
            dsNewCollObjs = getDsObjFromTitle(sNewCollTitle, sCollClassName,new String[] {dsSourceColl.getHandle().toString()});
            
            if(dsNewCollObjs == null) {
                System.out.println("dsNewCollObjs: NULL");
                
                // Get the class of the object we want created
                DSClass dsCollClassName = dsSession.getDSClass( DSCollection.classname );

                // Create a prototype
                DSProperties dsCollProps = dsCollClassName.createPrototype(); 
                
                String sMtd = "[createColl]";

                // Set the required properties
                System.out.println(sMtd + "INFO:- setPropValue (" + sTitlePropName + " = " + sNewCollTitle +")" );
                dsCollProps.setPropValue( sTitlePropName, sNewCollTitle );                               
                
                if(dsSourceColl == null) {
                    throw new Exception("[createColl] ERROR:- dsSourceColl: NULL");
                }
                
                // create the collection                
                System.out.println(sMtd + "INFO:- createObject" );
                DSHandle dsNewCollHandle = dsSession.createObject( dsCollProps, DSLinkDesc.containment, dsSourceColl, null, null );
                //working code using variable dsNewCollObj
                DSObject dsNewCollObj = dsSession.getObject(dsNewCollHandle);
                                
                //working code for displaying created collection
                //System.out.println("Created Collection : " + dsNewCollObj.getTitle() + " ("+ dsNewCollHandle.toString() + ")");
                
                //freds working code for displaying created collection
                System.out.println("");
                System.out.println("test1");
                System.out.println("Created Collection : " + dsNewCollObj.getTitle() + " ("+ dsNewCollHandle.toString() + ")" + " [ "+dsNewCollObj.getModifiedDate()+" ] " +dsNewCollObj.getObjectLocale());
                
                //Return to Main Menu
                System.out.println("");
                this.getOptionsFromCmd();
                
            }//if(dsNewCollObjs == null)
            
            if(dsNewCollObjs.length == 1) {     
                //Display Error Message if New Collection Title already exists
                System.out.println("\n\nWARNING : SINGLE COLLECTION FOUND WITH TITLE: " + sNewCollTitle);
                      
                //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                this.displayResultsForNewCollTitle();
                
                //Display an Invalid Option message if the user selects an Option that is outside the Selection Range
                while(nOption2 <= dsNewCollObjs.length || nOption2 > dsNewCollObjs.length + 3) {                    
                    
                    System.out.println("\n" + nOption2 + " is an Invalid Option");
                    System.out.println("\nYou Can Only Select Among Options: " + "[" + (dsNewCollObjs.length + 1) + " - " + (dsNewCollObjs.length + 3) + "]");
                                        
                    //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                    this.displayResultsForNewCollTitle();                                                          
                } 
                
                //Option Selected to Enter New Collection Title
                while(nOption2 == dsNewCollObjs.length + 1) {                    
                                 
                    sNewCollTitle = System.console().readLine("\nEnter New Collection Title: ");
            
                    System.out.println("[getDsObjFromTitle(" + sNewCollTitle + ", " + sCollClassName + ", " + dsSourceColl.getHandle().toString() +")]");                    
                    dsNewCollObjs = getDsObjFromTitle(sNewCollTitle, sCollClassName,new String[] {dsSourceColl.getHandle().toString()});
                    //********************************************the immediate code below this line hass been added*********************************************

                    if(dsNewCollObjs.length == 1){
                        //Display Error Message if New Collection Title already exists
                        System.out.println("\n\nWARNING : SINGLE COLLECTION FOUND WITH TITLE: " + sNewCollTitle);
                        
                        //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                        this.displayResultsForNewCollTitle();                                     
                    }// if(dsNewCollObjs.length == 1)                      
                    
                    if(dsNewCollObjs.length > 1){
                        //Display Error Message if New Collection Title already exists
                        System.out.println("\n\nWARNING : MULTIPLE COLLECTIONS FOUND WITH TITLE: " + sNewCollTitle);
                        
                        //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                        this.displayResultsForNewCollTitle();                                     
                    }// if(dsNewCollObjs.length > 1)                                                              
                    
                    if(dsNewCollObjs == null) {

                        System.out.println("dsNewCollObjs: NULL");

                        // Get the class of the object we want created
                        DSClass dsCollClassName = dsSession.getDSClass( DSCollection.classname );

                        // Create a prototype
                        DSProperties dsCollProps = dsCollClassName.createPrototype(); 

                        String sMtd = "[createColl]";

                        // Set the required properties
                        System.out.println(sMtd + "INFO:- setPropValue(" + sTitlePropName + ", " + sNewCollTitle +")" );
                        dsCollProps.setPropValue( sTitlePropName, sNewCollTitle );       


                        if(dsSourceColl == null) {
                            throw new Exception("[createColl] ERROR:- dsSourceColl: NULL");
                        }

                        // create the collection                        
                        System.out.println(sMtd + "INFO:- createObject" );
                        DSHandle dsNewCollHandle = dsSession.createObject( dsCollProps, DSLinkDesc.containment, dsSourceColl, null, null );
                        DSObject dsNewCollObj = dsSession.getObject(dsNewCollHandle);
                        System.out.println("Created Collection: " + dsNewCollObj.getTitle() + " ("+ dsNewCollHandle.toString() + ")"); 

                        //Return to Main Menu
                        this.getOptionsFromCmd();
                        
                    } //if(dsNewCollObjs == null)

                    //Display an Invalid Option message if the user selects an Option that is outside the Selection Range
                    while(nOption2 <= dsNewCollObjs.length || nOption2 > dsNewCollObjs.length + 3) {                    
                        
                        System.out.println("\n" + nOption2 + " is an Invalid Option");
                        System.out.println("\nYou Can Only Select Among Options: " + "[" + (dsNewCollObjs.length + 1) + " - " + (dsNewCollObjs.length + 3) + "]");

                        //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                        this.displayResultsForNewCollTitle();                                                                            
                    }// while(nOption2 <= dsNewCollObjs.length || nOption2 > dsNewCollObjs.length + 3)                     
                    
                }// while(nOption2 == dsNewCollObjs.length + 1)
                
                //********************************************the immediate code above this line has been added****************************************************************************  
                                
                //Option to Create Collection With The Same Collection Title
                if(nOption2 == dsNewCollObjs.length + 2) {

                    //Enter Code
                    System.out.println("Option to Create Collection With The Same Collection Title");
                    // Get the class of the object we want created
                    DSClass dsCollClassName = dsSession.getDSClass( DSCollection.classname );

                    // Create a prototype
                    DSProperties dsCollProps = dsCollClassName.createPrototype(); 

                    // Set the required properties                        
                    dsCollProps.setPropValue( sTitlePropName, sNewCollTitle );

                    // create the collection                        
                    DSHandle dsNewCollHandle = dsSession.createObject( dsCollProps, DSLinkDesc.containment, dsSourceColl, null, null );
                    DSObject dsNewCollObj = dsSession.getObject(dsNewCollHandle);
                    System.out.println("test2");
                    System.out.println("Created Collection: " + dsNewCollObj.getTitle() + " ("+ dsNewCollHandle.toString() + ")");

                    //Return to Main Menu
                    this.getOptionsFromCmd();
                }//if(nOption2 == dsNewCollObjs.length + 2)
                                
                //Option Selected to Return to Main Menu
                if(nOption2 == dsNewCollObjs.length + 3) {
                    //Return to Main Menu
                    this.getOptionsFromCmd();                
                }// if(nOption2 == dsNewCollObjs.length + 3)                                                                                               
            }// if(dsNewCollObjs.length == 1)            
                                            
            if(dsNewCollObjs.length > 1) {     
                //Display Error Message if New Collection Title already exists
                System.out.println("\n\nWARNING : MULTIPLE COLLECTIONS FOUND WITH TITLE: " + sNewCollTitle);
                      
                //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                this.displayResultsForNewCollTitle();
                
                //Display an Invalid Option message if the user selects an Option that is outside the Selection Range
                while(nOption2 <= dsNewCollObjs.length || nOption2 > dsNewCollObjs.length + 3) {                    
                    
                    System.out.println("\n" + nOption2 + " is an Invalid Option");
                    System.out.println("\nYou Can Only Select Among Options: " + "[" + (dsNewCollObjs.length + 1) + " - " + (dsNewCollObjs.length + 3) + "]");
                                        
                    //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                    this.displayResultsForNewCollTitle();                                                          
                } 
                
                //Option Selected to Enter New Collection Title
                while(nOption2 == dsNewCollObjs.length + 1) {                    
                                 
                    sNewCollTitle = System.console().readLine("\nEnter New Collection Title: ");
            
                    System.out.println("[getDsObjFromTitle(" + sNewCollTitle + ", " + sCollClassName + ", " + dsSourceColl.getHandle().toString() +")]");                    
                    dsNewCollObjs = getDsObjFromTitle(sNewCollTitle, sCollClassName,new String[] {dsSourceColl.getHandle().toString()});
                    //********************************************the immediate code below this line hass been added*********************************************
                    
                    if(dsNewCollObjs.length == 1){
                        //Display Error Message if New Collection Title already exists
                        System.out.println("\n\nWARNING : SINGLE COLLECTION FOUND WITH TITLE: " + sNewCollTitle);
                        
                        //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                        this.displayResultsForNewCollTitle();                                     
                    }// if(dsNewCollObjs.length == 1)                    
                    
                    if(dsNewCollObjs.length > 1){
                        //Display Error Message if New Collection Title already exists
                        System.out.println("\n\nWARNING : MULTIPLE COLLECTIONS FOUND WITH TITLE: " + sNewCollTitle);
                        
                        //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                        this.displayResultsForNewCollTitle();                                     
                    }// if(dsNewCollObjs.length > 1)                                                              
                    
                    if(dsNewCollObjs == null) {

                        System.out.println("dsNewCollObjs: NULL");

                        // Get the class of the object we want created
                        DSClass dsCollClassName = dsSession.getDSClass( DSCollection.classname );

                        // Create a prototype
                        DSProperties dsCollProps = dsCollClassName.createPrototype(); 

                        String sMtd = "[createColl]";

                        // Set the required properties
                        System.out.println(sMtd + "INFO:- setPropValue(" + sTitlePropName + ", " + sNewCollTitle +")" );
                        dsCollProps.setPropValue( sTitlePropName, sNewCollTitle );       


                        if(dsSourceColl == null) {
                            throw new Exception("[createColl] ERROR:- dsSourceColl: NULL");
                        }

                        // create the collection                        
                        System.out.println(sMtd + "INFO:- createObject" );
                        DSHandle dsNewCollHandle = dsSession.createObject( dsCollProps, DSLinkDesc.containment, dsSourceColl, null, null );
                        DSObject dsNewCollObj = dsSession.getObject(dsNewCollHandle);
                        System.out.println("test3");
                        System.out.println("Created Collection: " + dsNewCollObj.getTitle() + " ("+ dsNewCollHandle.toString() + ")"); 

                        //Return to Main Menu
                        this.getOptionsFromCmd();
                        
                    } //if(dsNewCollObjs == null)

                    //Display an Invalid Option message if the user selects an Option that is outside the Selection Range
                    while(nOption2 <= dsNewCollObjs.length || nOption2 > dsNewCollObjs.length + 3) {                    
                        
                        System.out.println("\n" + nOption2 + " is an Invalid Option");
                        System.out.println("\nYou Can Only Select Among Options: " + "[" + (dsNewCollObjs.length + 1) + " - " + (dsNewCollObjs.length + 3) + "]");

                        //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                        this.displayResultsForNewCollTitle();                                                                            
                    }                     
                    
                }   //********************************************the immediate code above this line has been added****************************************************************************  
                                
                //Option to Create Collection With The Same Collection Title
                if(nOption2 == dsNewCollObjs.length + 2) {

                    //Enter Code
                    System.out.println("Option to Create Collection With The Same Collection Title");
                    // Get the class of the object we want created
                    DSClass dsCollClassName = dsSession.getDSClass( DSCollection.classname );

                    // Create a prototype
                    DSProperties dsCollProps = dsCollClassName.createPrototype(); 

                    // Set the required properties                        
                    dsCollProps.setPropValue( sTitlePropName, sNewCollTitle );

                    // create the collection                        
                    DSHandle dsNewCollHandle = dsSession.createObject( dsCollProps, DSLinkDesc.containment, dsSourceColl, null, null );
                    DSObject dsNewCollObj = dsSession.getObject(dsNewCollHandle);
                    System.out.println("test4");
                    System.out.println("Created Collection: " + dsNewCollObj.getTitle() + " ("+ dsNewCollHandle.toString() + ")");

                    //Return to Main Menu
                    this.getOptionsFromCmd();
                }// if(nOption2 == dsNewCollObjs.length + 2)
                                
                //Option Selected to Return to Main Menu
                if(nOption2 == dsNewCollObjs.length + 3) {
                    //Return to Main Menu
                    this.getOptionsFromCmd();                
                }// if(nOption2 == dsNewCollObjs.length + 3)                                                                                               
            }//if(dsNewCollObjs.length > 1)                      
        }        
        catch(Exception E) {
            throw E;
        }        
    }
    
    // Method for Uploading a File
    public void UploadDocument() throws Exception{
        
        DSCollection dsTargetColl = null;
        DSDocument dsDocumentTitle = null;
                
        try {
            System.out.println("\n\nUPLOAD DOCUMENT\n");            
            sTargetCollTitle = System.console().readLine("\nEnter the Source Collection Title: ");
            System.out.println("[getDsObjFromTitle(" + sTargetCollTitle + ", " + sCollClassName + ", " + "null" +"]");
            DSObject[] dsTargetCollObjs = getDsObjFromTitle(sTargetCollTitle,sCollClassName,null);
            
            if(dsTargetCollObjs == null){
                System.out.println("\n\nCOLLECTION: " + "[" + sTargetCollTitle + "]" + "has not been found\n");                  
                
                //The sub menu 
                System.out.println("1. Enter New Source Collection Title  \n");
                System.out.println("2. Return to Main Menu  \n"); 
                
                sOption = System.console().readLine("Enter Option: ");
                                
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                               
                switch(nOption) {
                    case 1:
                        System.out.println("Processing Option 1");
                        this.UploadDocument();
                        break;
                    case 2:
                        System.out.println("Processing Option 2");
                        this.getOptionsFromCmd();
                        break;                          
                    default:
                        System.out.println(nOption + " is an Invalid Option");                        
                        this.getOptionsFromCmd();
                        break;                
                }
                
            }
            
            //Multiple source collections found with given title
            if (dsTargetCollObjs.length > 1) {
                
                //loop through dsObjs array and construct a ordered numbered sequence of options for the user to choose from.
                //dont forget to append n+1 and n+2
                
                //Inform user multiple collections foind with title: <var>
                System.out.println("\n\nMULTIPLE COLLECTIONS FOUND WITH TITLE: " + sTargetCollTitle + "\n" );
                
                //we are going to display the list of collections with result
                for (int i = 0; i < dsTargetCollObjs.length; i++) {
                    System.out.println("\n" + (i + 1 ) + ". " + dsTargetCollObjs[i].getTitle() + "(" + dsTargetCollObjs[i].getHandle().toString() + ")");
                }
                System.out.println("\n" + (dsTargetCollObjs.length + 1 ) +  ". Enter Source Collection Title");
                System.out.println("\n" + (dsTargetCollObjs.length + 2 ) +  ". Return to Main Menu");
                
                sOption = System.console().readLine("\nOption: ");
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                
                //One of the multiple locations has been selected by the user
                if(nOption >= 1 && nOption <= dsTargetCollObjs.length) {
                    dsTargetColl = (DSCollection) dsTargetCollObjs[nOption-1];
                }
                else {
                    //Option Selected for Enter Source Collection Title Again
                    if(nOption == dsTargetCollObjs.length + 1) {
                        this.UploadDocument();
                    }
                    else {
                        //Option Selected to Return to Main Menu
                        if(nOption == dsTargetCollObjs.length + 2) {
                            this.getOptionsFromCmd();
                        }
                        else {                            
                            System.out.println("\n" + nOption + " Is an Invalid Option ");
                        }// if(nOption == dsTargetCollObjs.length + 1)
                    }// if(nOption == dsTargetCollObjsObjs.length + 2)
                }// if(nOption >= 1 and nOption <= dsTargetCollObjs.length)
            }//if (dsTargetCollObjs.length > 1)          
            
            if (dsTargetCollObjs.length == 1) {
                //Inform user single collection found with title: 
                System.out.println("\nSINGLE COLLECTION FOUND WITH TITLE: " + sTargetCollTitle + "\n" );
                dsTargetColl = (DSCollection) dsTargetCollObjs[0];
                            
                //we are going to display the list of collections with result
                for (int i = 0; i < dsTargetCollObjs.length; i++) {
                    System.out.println("\n" + (i + 1 ) + ". " + dsTargetCollObjs[i].getTitle() + "(" + dsTargetCollObjs[i].getHandle().toString() + ")");
                                       
                }
            }//if (dsTargetCollObjs.length == 1)            
            
            //Enter the Document Title
            sDocTitle = System.console().readLine("\nEnter the Document Title: ");
            
            System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + " ," + dsTargetColl.getHandle().toString()+ ")]");
            dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,new String[] {dsTargetColl.getHandle().toString()});
                       
            if(dsDocObjs == null) {
                
                System.out.println("dsDocObjs: NULL");
          
                sSrcFile = System.console().readLine("\nEnter the Path of File to Upload: ");
                 
                // ------------------------------------------------------------
                // Prepare document properties, Version properties and Rendition
                // properties before Upload
                // ------------------------------------------------------------

                // Prepare class object for upload
                DSClass docClass = dsSession.getDSClass( DSDocument.classname );

                // Prepare Document Properties
                DSProperties docProp = docClass.createPrototype();

                // Prepare Version Properties
                DSClass versionClass = dsSession.getDSClass( DSVersion.classname );
                DSProperties versionProp = versionClass.createPrototype();

                // Add a title to the doc being uploaded
                versionProp.setPropValue( sTitlePropName, sDocTitle );
                
                // Create Rendition Properties
                DSClass renditionClass = dsSession.getDSClass( DSRendition.classname );
                DSProperties renditionProp = renditionClass.createPrototype();
                renditionProp.setPropValue( sTitlePropName,
                        "Uploaded rendition by DSCreateWorkspace" );

                // Define the source file
                File newFile = new File( sSrcFile ).getAbsoluteFile();

                // Assign the content into DSContentElement
                DSContentElement content = new FileContentElement(
                        newFile.getPath(), false );
                docProp.setPropValue( sTitlePropName, sDocTitle );

                System.out.println( "Uploading [" + newFile + "]" );
                System.out.println( "To: " + dsTargetColl.getTitle() + " ["
                        + dsTargetColl.getHandle() + "]" );

                // Make a create Document request to the DocuShare server
                DSHandle NewDocHandle = dsSession.createDocument( docProp,
                        versionProp, renditionProp,
                        new DSContentElement[] { content }, null,
                        DSLinkDesc.containment, dsTargetColl, null, null );
                
                DSObject dsDocObj = dsSession.getObject(NewDocHandle);
                
                System.out.println( "Created Document: " + dsDocObj.getTitle() + " (" + dsDocObj.getHandle() + ")");              
                 
                //Return to Main Menu
                this.getOptionsFromCmd();
                                      
            }//if(dsDocObjs == null)           
             
            if(dsDocObjs.length >= 1) {  
                
                //Display Error Message if Document Title already exists
                System.out.println("\n\nWARNING : DOCUMENT(S) " + "[" + sDocTitle + "] " + "ALREADY EXIST");
                //dsDocumentTitle = (DSDocument) dsDocObjs[0];
                                                                                
                //Display results of the Document Title Entered and append the Sub Menu if dsDocObjs.length >= 1
                this.displayResultsForDocTitle(); 
                
                //Display an Invalid Option message if the user selects an Option that is outside the Selection Range
                while(nOption2 <= dsDocObjs.length || nOption2 > dsDocObjs.length + 3) {                    
                    
                    System.out.println("\n" + nOption2 + " is an Invalid Option");
                    System.out.println("\nYou Can Only Select Among Options: " + "[" + (dsDocObjs.length + 1) + " - " + (dsDocObjs.length + 3) + "]");
                                        
                    //Display results of the New Collection Title Entered and append the Sub Menu if dsNewCollObjs.length >= 1
                    this.displayResultsForDocTitle();                                                          
                }                
                                
                //Option Selected to Enter Document Title
                while(nOption2 == dsDocObjs.length + 1) {
                                        
                    sDocTitle = System.console().readLine("\nEnter the Document Title: ");

                    System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + ", " + dsTargetColl.getHandle().toString() +")]");
                    dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,new String[] {dsTargetColl.getHandle().toString()});
                    
                    //*****************************the immediate code below this code has been added ************************************                                             
                                                        
                    if(dsDocObjs == null) {
                        
                        sSrcFile = System.console().readLine("\nEnter the Path of File to Upload: ");
                    
                        // Prepare class object for upload
                        DSClass docClass = dsSession.getDSClass( DSDocument.classname );

                        // Prepare Document Properties
                        DSProperties docProp = docClass.createPrototype();

                        // Prepare Version Properties
                        DSClass versionClass = dsSession.getDSClass( DSVersion.classname );
                        DSProperties versionProp = versionClass.createPrototype();

                        // Add a title to the doc being uploaded
                        versionProp.setPropValue( sTitlePropName, sDocTitle );

                        // Create Rendition Properties
                        DSClass renditionClass = dsSession.getDSClass( DSRendition.classname );
                        DSProperties renditionProp = renditionClass.createPrototype();
                        renditionProp.setPropValue( sTitlePropName,
                                "Uploaded rendition by DSCreateWorkspace" );

                        // Define the source file
                        File newFile = new File( sSrcFile ).getAbsoluteFile();

                        // Assign the content into DSContentElement
                        DSContentElement content = new FileContentElement(
                                newFile.getPath(), false );
                        docProp.setPropValue( sTitlePropName, sDocTitle );

                        System.out.println( "Uploading [" + newFile + "]" );
                        System.out.println( "To: " + dsTargetColl.getTitle() + " ["
                                + dsTargetColl.getHandle() + "]" );

                        // Make a create Document request to the DocuShare server
                        DSHandle NewDocHandle = dsSession.createDocument( docProp,
                                versionProp, renditionProp,
                                new DSContentElement[] { content }, null,
                                DSLinkDesc.containment, dsTargetColl, null, null );

                        DSObject dsDocObj = dsSession.getObject(NewDocHandle);

                        System.out.println( "Created Document: " + dsDocObj.getTitle() + " (" + dsDocObj.getHandle() + ")");              

                        //Return to Main Menu
                        this.getOptionsFromCmd(); 
                        
                    }// if(dsDocObjs == null) 
                    
                    if(dsDocObjs.length >= 1){
                        //Display Error Message if Document Title already exists
                        System.out.println("\n\nWARNING : DOCUMENT(S) " + "[" + sDocTitle + "] " + "ALREADY EXIST");
                        
                        //Display results of the Document Title Entered and append the Sub Menu if dsNewCollObjs.length > 1
                        this.displayResultsForDocTitle();                                     
                    }// if(dsDocObjs.length >= 1)
                                        
                    //Display an Invalid Option message if the user selects an Option that is outside the Selection Range
                    while(nOption2 <= dsDocObjs.length || nOption2 > dsDocObjs.length + 3) {                    
                        
                        System.out.println("\n" + nOption2 + " is an Invalid Option");
                        System.out.println("\nYou Can Only Select Among Options: " + "[" + (dsDocObjs.length + 1) + " - " + (dsDocObjs.length + 3) + "]");

                        //Display results of the Document Title Entered and append the Sub Menu 
                        this.displayResultsForDocTitle();                                                                            
                    }                   
                    
                } //while(nOption2 == dsDocObjs.length + 1)
                
                //Option to Create Document With The Same Document Title
                if (nOption2 == dsDocObjs.length + 2) {

                    sSrcFile = System.console().readLine("\nEnter the Path of File to Upload: ");

                    // Prepare class object for upload
                    DSClass docClass = dsSession.getDSClass( DSDocument.classname );

                    // Prepare Document Properties
                    DSProperties docProp = docClass.createPrototype();

                    // Prepare Version Properties
                    DSClass versionClass = dsSession.getDSClass( DSVersion.classname );
                    DSProperties versionProp = versionClass.createPrototype();

                    // Add a title to the doc being uploaded
                    versionProp.setPropValue( sTitlePropName, sDocTitle );

                    // Create Rendition Properties
                    DSClass renditionClass = dsSession.getDSClass( DSRendition.classname );
                    DSProperties renditionProp = renditionClass.createPrototype();
                    renditionProp.setPropValue( sTitlePropName,
                            "Uploaded rendition by DSCreateWorkspace" );

                    // Define the source file
                    File newFile = new File( sSrcFile ).getAbsoluteFile();

                    // Assign the content into DSContentElement
                    DSContentElement content = new FileContentElement(
                            newFile.getPath(), false );
                    docProp.setPropValue( sTitlePropName, sDocTitle );

                    System.out.println( "Uploading [" + newFile + "]" );
                    System.out.println( "To: " + dsTargetColl.getTitle() + " ["
                            + dsTargetColl.getHandle() + "]" );

                    // Make a create Document request to the DocuShare server
                    DSHandle NewDocHandle = dsSession.createDocument( docProp,
                            versionProp, renditionProp,
                            new DSContentElement[] { content }, null,
                            DSLinkDesc.containment, dsTargetColl, null, null );

                    DSObject dsDocObj = dsSession.getObject(NewDocHandle);

                    System.out.println( "Created Document: " + dsDocObj.getTitle() + " (" + dsDocObj.getHandle() + ")");              

                    //Return to Main Menu
                    this.getOptionsFromCmd();   

                }// if (nOption2 == dsDocObjs.length + 2)
                                
                //Option Selected to Return to Main Menu
                if(nOption2 == dsDocObjs.length + 3) {
                    //Return to Main Menu
                    this.getOptionsFromCmd();                
                }// if(nOption2 == dsDocObjs.length + 3)             
            }// if(dsDocObjs.length >= 1)
                      
        } catch ( Exception E ) {
           throw E;
        } 
    }
    
   // Perform Search
    public void SearchDocument() throws Exception {
        DSCollection dsTargetColl = null;
        DSDocument dsDocumentTitle = null;
                
        try {
            System.out.println("\n\nSEARCH DOCUMENT\n");            
            sNewTargetCollTitle = System.console().readLine("\nEnter the Root Collection Title: ");
            DSObject[] dsTargetCollObjs = getDsObjFromTitle(sNewTargetCollTitle,sCollClassName,null);
            
            if(dsTargetCollObjs == null){
                System.out.println("\n\nCOLLECTION: " + "[" + sNewTargetCollTitle + "]" + " has not been found\n");                  
                
                //The sub menu 
                System.out.println("1. Enter Root Collection Title \n");
                System.out.println("2. Return to Main Menu  \n"); 
                
                sOption = System.console().readLine("Enter Option: ");                                
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                               
                switch(nOption) {
                    case 1:
                        System.out.println("Processing Option 1");
                        this.SearchDocument();
                        break;
                    case 2:
                        System.out.println("Processing Option 2");
                        this.getOptionsFromCmd();
                        break;                          
                    default:
                        System.out.println("Invalid Option");                        
                        break;                
                }
                
            }
            
            //Multiple source collections found with given title
            if (dsTargetCollObjs.length > 1) {
                
                //loop through dsObjs array and construct a ordered numbered sequence of options for the user to choose from.
                //dont forget to append n+1 and n+2
                
                //Inform user multiple collections foind with title: <var>
                System.out.println("\n\nMULTIPLE COLLECTIONS FOUND WITH TITLE: " + sTargetCollTitle + "\n" );
                
                //we are going to display the list of collections with result
                for (int i = 0; i < dsTargetCollObjs.length; i++) {
                    System.out.println("\n" + (i + 1 ) + ". " + dsTargetCollObjs[i].getTitle() + "(" + dsTargetCollObjs[i].getHandle().toString() + ")");
                }
                System.out.println("\n" + (dsTargetCollObjs.length + 1 ) +  ". Enter the Root Collection Title Again");
                System.out.println("\n" + (dsTargetCollObjs.length + 2 ) +  ". Return to Main Menu");
                sOption = System.console().readLine("\nOption: ");
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                
                //One of the multiple locations has been selected by the user
                if(nOption >= 1 && nOption <= dsTargetCollObjs.length) {
                    dsTargetColl = (DSCollection) dsTargetCollObjs[nOption-1];
                }
                else {
                    //Option Selected for Enter Source Collection Title Again
                    if(nOption == dsTargetCollObjs.length + 1) {
                        this.SearchDocument();
                    }
                    else {
                        //Option Selected to Return to Main Menu
                        if(nOption == dsTargetCollObjs.length + 2) {
                            this.getOptionsFromCmd();
                        }
                        else {
                            System.out.println(sOption + " is an Invalid Option");                            
                            
                        }//  if(nOption == dsTargetCollObjs.length + 2)
                    }// if(nOption == dsTargetCollObjs.length + 1)
                }// if(nOption >= 1 && nOption <= dsTargetCollObjs.length)
            }//if (dsTargetCollObjs.length > 1)          
            
            if (dsTargetCollObjs.length == 1) {
                
                //Inform user single collections found with title: <var>
                System.out.println("\nSINGLE COLLECTION FOUND WITH TITLE: " + sNewTargetCollTitle + "\n" );
                dsTargetColl = (DSCollection) dsTargetCollObjs[0];
                            
                //we are going to display the list of collections with result
                for (int i = 0; i < dsTargetCollObjs.length; i++) {
                    System.out.println("\n" + (i + 1 ) + ". " + dsTargetCollObjs[i].getTitle() + "(" + dsTargetCollObjs[i].getHandle().toString() + ")");
                                       
                }
            }//if (dsTargetCollObjs.length == 1)   
            
            //Enter the Document Title
            sDocTitle = System.console().readLine("\nEnter the Document Title: ");
            
            System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + ", null)]");
            dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,null);
            
            if(dsDocObjs == null) {
                
                System.out.println("\n\nDOCUMENT: " + "[" + sDocTitle + "]" + " has not been found\n");                  
                                
                //Display submenu if document title is null
                //this.checkIfDocTitleIsNull();
                
                //Option Selected To Enter Document Title Again
                if(nNullDocTitle ==  1) {

                    sDocTitle = System.console().readLine("\nEnter Document Title: ");

                    System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + ", null)]");
                    dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,null);
                    
                    if (dsDocObjs == null) {
                        System.out.println("\n\nDOCUMENT: " + "[" + sDocTitle + "]" + " has not been found\n");
                        
                        //Display submenu if document title is null
                        //this.checkIfDocTitleIsNull();
                    }
                    else{
                        
                        if(dsDocObjs != null){
                            //Display submenu if document title is not null
                            //this.checkIfDocTitleIsNotNull();
                            
                            if(nOption == dsDocObjs.length + 1) {

                                sDocTitle = System.console().readLine("\nEnter Document Title: ");

                                System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + ", null)]");
                                dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,null);

                                System.out.println("\n\nDOCUMENT: " + "[" + sDocTitle + "]" + " has not been found\n");                  

                                //Display submenu if document title is null
                                //this.checkIfDocTitleIsNull();
                            }
                            else {
                                if (nOption == dsDocObjs.length + 2){
                                    this.getOptionsFromCmd();                            
                                }
                                else{                            
                                    System.out.println("Document " + sDocTitle + " Does Not Exist");                            
                                }                   
                            }                         
                        }                        
                    }                    
               }                
                else {
                    //Option Selected to Return to Main Menu
                    if(nNullDocTitle == 2) {
                        this.getOptionsFromCmd();
                    }
                    else {
                        System.out.println(sOption + " is an Invalid Option");
                        }// if(nOption == 2)
                    }// if(nOption == 1)                
                }// if(dsDocObjs == null)                      
                   
            if(dsDocObjs.length >= 1) {
                
                for (int x = 0; x < dsDocObjs.length; x++) {
                    
                    System.out.println("\n" + (x + 1 ) + ". " + "Found Document:  "  + dsDocObjs[x].getTitle() + "(" + dsDocObjs[x].getHandle().toString() + ")"); 
                                                                
                }        
                System.out.println("\n" + (dsDocObjs.length + 1) +  ". Enter a New Document Title ");                                   
                System.out.println("\n" + (dsDocObjs.length + 2) +  ". Return to Main Menu");
                
                sOption = System.console().readLine("\nSelect an Option: ");
                Integer iOption = new Integer(sOption);
                nOption = iOption.intValue();
                
                //One of the multiple locations has been selected by the user
                if(nOption >= 1 && nOption <= dsDocObjs.length) {
                  dsDocumentTitle = (DSDocument) dsDocObjs[nOption-1];
                }
                else {
                    //Option Selected to Enter Document Title Again
                    if(nOption == dsDocObjs.length + 1) {
                        sDocTitle = System.console().readLine("\nEnter a New Document Title: ");

                        System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + ", null)]");
                        dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,null);

                        if(dsDocObjs != null) {
                            for (int x = 0; x < dsDocObjs.length; x++) {

                                System.out.println("\n" + (x + 1 ) + ". " + "Found Document:  "  + dsDocObjs[x].getTitle() + "(" + dsDocObjs[x].getHandle().toString() + ")"); 

                            }   
                            System.out.println("\n" + (dsDocObjs.length + 1) +  ". Return to Main Menu");

                            sOption = System.console().readLine("\nSelect an Option: ");
                            nOption = iOption.intValue();
                            
                            if(nOption == dsDocObjs.length + 1){
                                this.getOptionsFromCmd();
                            }                           

                        }
                        else{
                            System.out.println("\n\nDOCUMENT: " + "[" + sDocTitle + "]" + "has not been found\n");
                                                                                   
                            sDocTitle = System.console().readLine("\nEnter a New Document Title: ");

                            System.out.println("[getDsObjFromTitle(" + sDocTitle + ", " + sDocClassName + ", null)]");
                            dsDocObjs = getDsObjFromTitle(sDocTitle, sDocClassName,null);
                            

                            if(dsDocObjs != null) {
                                for (int x = 0; x < dsDocObjs.length; x++) {

                                    System.out.println("\n" + (x + 1 ) + ". " + "Found Document:  "  + dsDocObjs[x].getTitle() + "(" + dsDocObjs[x].getHandle().toString() + ")"); 

                                }   
                                System.out.println("\n" + (dsDocObjs.length + 1) +  ". Return to Main Menu");

                                sOption = System.console().readLine("\nSelect an Option: ");
                                nOption = iOption.intValue();

                                if(nOption == dsDocObjs.length + 1){
                                    //Return to Main Menu
                                    this.getOptionsFromCmd();
                                    }
                                }   
                            else{
                                System.out.println("\n\nDOCUMENT: " + "[" + sDocTitle + "]" + "has not been found\n");
                                
                                //Returns the user to the MENU of the MAIN PROGRAM if the search result returns a NULL for a second time
                                System.out.println("Returning to the MAIN MENU: ");
                                //Return to Main Menu
                                this.getOptionsFromCmd(); 
                                }                           
                            }
                        }
                    else{    
                        
                        //Option to Create Document With The Same Title
                        if(nOption == dsDocObjs.length + 2) {
                            //Return to Main Menu
                            this.getOptionsFromCmd();
                            }                     
                        else { 

                            System.out.println(nOption + " Is An Invalid Option");  

                            }// if(nOption == dsDocObjs.length + 2)

                      }// if(nOption == dsDocObjs.length + 1)                
                                                                                            
                  }//if(nOption >= 1 && nOption <= dsDocObjs.length)                      
             }//if(dsDocObjs.length >= 1)
                       

        } catch ( Exception E ) {
           throw E;
        } 
    }
    
    /**
     * Display options to user and set class var nOption
     */
    public void getOptionsFromCmd() throws Exception {
        System.out.println("");
        System.out.println("MAIN PROGRAM: SELECT OPTION\n");
        System.out.println("1.    Create Collection  \n");
        System.out.println("2.    Upload Document  \n");
        System.out.println("3.    Find Document  \n");
        System.out.println("4.    Exit  \n");
              
        try {
            
            sOption = System.console().readLine("Enter Option: ");
            System.out.println("");
            System.out.println("You have entered option: " + sOption);
            System.out.println("");
            
            Integer iOption = new Integer(sOption);
            nOption = iOption.intValue();            
        }
        catch(NumberFormatException E) {
            throw E;
        }        
    }
    
    public void ProcessOptions() throws Exception {
        System.out.println("\nPROCESS OPTION\n");
        switch(nOption) {
            case 1:
                System.out.println("Processing Option 1");
                this.createColl();
                break;
            case 2:
                System.out.println("Processing Option 2");
                this.UploadDocument();
                break;
            case 3:
                System.out.println("Processing Option 3");
                this.SearchDocument();
                break;
            case 4:
                System.out.println("Processing Option 4");
                this.finalize();
                break;            
            default:
                System.out.println(nOption + " is an Invalid Option"); 
                this.getOptionsFromCmd();
                break;
        }
    }
            
    public void displayResultsForNewCollTitle()throws Exception{
        
        try {
                                                        
            for (int x = 0; x < dsNewCollObjs.length; x++) {

                System.out.println("\n" + (x + 1 ) + ". " + dsNewCollObjs[x].getTitle() + " (" + dsNewCollObjs[x].getHandle().toString() + ")");
            }

            System.out.println("\n" + (dsNewCollObjs.length + 1) +  ". Enter  Collection Title ");
            System.out.println("\n" + (dsNewCollObjs.length + 2) +  ". Continue to Create Collection With The Same Collection Title ");
            System.out.println("\n" + (dsNewCollObjs.length + 3) +  ". Return to Main Menu");

            sOption = System.console().readLine("\n\nSelect an Option: ");  
            Integer iOption = new Integer(sOption);
            nOption2 = iOption.intValue();                                              
            
        } catch (NumberFormatException E) {            
            throw E;
        }       
    }
    
    public void displayResultsForDocTitle()throws Exception {
                       
        try {            
                                            
            for (int x = 0; x < dsDocObjs.length; x++) {

                System.out.println("\n" + (x + 1 ) + ". " + dsDocObjs[x].getTitle() + " (" + dsDocObjs[x].getHandle().toString() + ")");
            }

            System.out.println("\n" + (dsDocObjs.length + 1) +  ". Enter Document Title ");
            System.out.println("\n" + (dsDocObjs.length + 2) +  ". Continue to Create Document With The Same Document Title ");
            System.out.println("\n" + (dsDocObjs.length + 3) +  ". Return to Main Menu");

            sOption = System.console().readLine("\n\nSelect an Option: ");  
            Integer iOption = new Integer(sOption);
            nOption2 = iOption.intValue();
        }
        catch(NumberFormatException E) {
            throw E;
        }       
    }
    
    public void getLoginDetailFromCmd() throws Exception {
               
        System.out.println("");
        System.out.println("LOGIN DETAILS\n");
               
        sHost = System.console().readLine("sHost: ");
        sDomain = System.console().readLine("sDomain: ");
        sUsername = System.console().readLine("sUsername: ");
        sPassword = System.console().readLine("sPassword: ");        
    }
    
    public void createDsSession() throws Exception {
        
        try {
            DSServer dsServer = DSFactory.createServer(sHost);
            dsSession = dsServer.createSession(sDomain, sUsername, sPassword);
            System.out.println("Login Session: " + dsSession.getLoginPrincipalHandle().toString());
        } 
        catch (Exception E) {
             E.printStackTrace();
        }
    }
    
    public void finalize() {
        
        try {
            if(dsSession!= null) {
                if(!dsSession.isClosed()) {
                    dsSession.close();
                    System.out.println("Logout Session: " + dsSession.getLoginPrincipalHandle().toString());
                }
                else {
                    System.out.println("\nLogout Session: No Session Open");
                }
            }
        }
        catch(Exception E) {
            E.getMessage();
        }
        
    }       

    public static void main(String[] args) {
        
        try {
        
            Fred011 f = new Fred011();
            f.getLoginDetailFromCmd();
            f.createDsSession();
            while (f.nOption != 4) {
                f.getOptionsFromCmd();
                System.out.println(f.nOption);
                f.ProcessOptions();
            }
            
            if(f.nOption == 4){
                f.finalize();  
                System.exit(0);
            }                             
                       
        }
        catch(Exception E) {
            E.printStackTrace();
        }
    }
}
