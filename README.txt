
Hi World! App

1. Create your new project in your favorite IDE.
2. Import japps-ui-common.jar to your project CLASSPATH
3. Also import third party libraries located into lib folder
4. In your main method add the following lines:

        DesktopApp.init("estadisticasdb", args); //It must be ALWAYS the first line in your project
        DesktopApp.start(new Label("Hi World"));

        /* new Label("Hola Mundo") It is the main component to show, it can be any 
           swing container, but always prefer to use the japps custom components */


Additionally:
------------------------------------------------------------------------------------

* Property management: 

    Create a properties files in package /res/config/ package with name <any name>.properties,
    example: /res/config/app.properties

    All properties in this file will be automatically loaded and can be used like:

            String value = Resources.p("your.propertiy.name");

    You can customize the next standar properties as you prefer:

            app.name=UI Common
            app.language=es
            app.iconset=gmaterial
            app.icon=accessibility.png
            app.enableSpeech=false
            app.fullscreen=false
            app.minimumsize=0,0,1,1
            #Class name of the look and feel, if value is "default" then the
            # standard java look and feel will be apllied
            app.lookandfeel=default
            app.font=Arial-BOLD-14
            app.debug=true



* Language management:

    Create a properties files, containing application text in package 
    /res/lang/<language: es=spanish, en=english>/<any name>.properties, example:
    /res/lang/es/lang.properties

    All properties in this file will be automatically loaded and can be used like:

            String text = Resources.$("your.key.text");

    You can set this standar values to custumize your App
    app.description=This is a default application description for Config system
    app.about= App detail

