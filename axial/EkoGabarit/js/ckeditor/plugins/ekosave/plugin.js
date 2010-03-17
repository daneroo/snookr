/*
 *I adapted this from instructions at:
 * http://stackoverflow.com/questions/1956967/ckeditor-ajax-save
 *
*/

/**
 * @axialsave plugin.
 */

(function()
{
    var saveCmd =
    {
        modes : {
            wysiwyg:1,
            source:1
        },

        exec : function( editor )
        {
            // not sure what this does.
            editor.updateElement();
            //alert('called axialsave');

            var ekoG = editor.ekoGabarit;
            if(!ekoG) return;
            if(!ekoG.save) return;
            ekoG.save();
        }
    };

    var pluginName = 'ekosave';

    // Register a plugin named "save".
    CKEDITOR.plugins.add( pluginName,
    {
        init : function( editor )
        {
            var command = editor.addCommand( pluginName, saveCmd );
            //command.modes = { wysiwyg : !!( editor.element.$.form ) };

            editor.ui.addButton( 'ekosave',
            {
                label : editor.lang.save,
                icon: 'skins/kama/icons.png',
                iconOffset: 2,
                command : pluginName
            });
        }
    });
})();
