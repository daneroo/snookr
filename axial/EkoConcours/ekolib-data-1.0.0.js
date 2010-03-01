/* 
 * This contains seed data for testing
 *  -- requires ekolib-constants (or it should)
 */


var contest1 = {
    "name":"Win a Free VisualStudio '05 Copy!",
    "steps":[{
        "intro":"Texte Intro Etape 1",
        "fields":[{
            "type":"EKO",
            "subtype":"prenom",
            "label":"Prénom",
            "validation":"required",
            "options": []
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"none",
            "options": []
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Occupation",
            "validation":"none",
            "options": []
        },{
            "type":"TAG",
            "subtype":"longtext",
            "label":"Commentaire",
            "validation":"none",
            "options": []
        },{
            "type":"CHOICE",
            "subtype":"check",
            "label":"Check Any",
            "validation":"none",
            "options": [
            {
                "name":"gr-french",
                "label":""
            },

            {
                "name":"gr-english",
                "label":"English"
            },

            {
                "name":"gr-child",
                "label":""
            },

            {
                "name":"gr-adult",
                "label":""
            },

            {
                "name":"gr-green",
                "label":"Enviro"
            },

            {
                "name":"gr-techsavy",
                "label":"Techie"
            }
            ]
        },{
            "type":"CHOICE",
            "subtype":"dropdown",
            "label":"Select One",
            "validation":"none",
            "options": [
            {
                "name":"gr-child",
                "label":""
            },

            {
                "name":"gr-adult",
                "label":""
            }
            ]
        }]

    },{
        "intro":"Texte Intro Etape 2",
        "fields":[{
            "type":"EKO",
            "subtype":"couriel",
            "label":"E-Mail",
            "validation":"required",
            "options": []
        },{
            "type":"EKO",
            "subtype":"prenom",
            "label":"Prénom",
            "validation":"none",
            "options": []
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"none",
            "options": []
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Occupation",
            "validation":"none",
            "options": []
        },{
            "type":"CHOICE",
            "subtype":"radio",
            "label":"Select One",
            "validation":"none",
            "options": [
            {
                "name":"gr-french",
                "label":""
            },

            {
                "name":"gr-english",
                "label":"English"
            }
            ]
        }]
    }]
};
