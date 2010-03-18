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
    var discardCmd =
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
            if(!ekoG.discard) return;
            ekoG.discardWithDialog();
        }
    };

    var pluginName = 'ekodiscard';

    // Register a plugin named "save".
    CKEDITOR.plugins.add( pluginName,
    {
        init : function( editor )
        {
            var command = editor.addCommand( pluginName, discardCmd );
            //command.modes = { wysiwyg : !!( editor.element.$.form ) };

            editor.ui.addButton( 'ekodiscard',
            {
                label : 'Discard',
                icon: 'skins/kama/icons.png',
                iconOffset: 13,
                command : pluginName
            });
        }
    });
})();
