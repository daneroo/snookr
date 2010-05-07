/* 
 * Depends on ekolib-x.x.x.js (for definition of namespace)
 */

eko.contest.FieldTypes = { // and subtypes
    EKO : [
    {
        name:"1j",
        label:"Nom"
    },
    {
        name:"j2",
        label:"Couriel"
    },
    {
        name:"o0",
        label:"Prénom"
    },
    {
        name:"h2",
        label:"Compagnie"
    },
    {
        name:"w2",
        label:"Adresse"
    },
    {
        name:"8v",
        label:"Adresse (Ligne 2)"
    },
    {
        name:"l2",
        label:"Ville" 
    },
    {
        name:"j5",
        label:"Province" 
    },
    {
        name:"0d",
        label:"Pays" 
    },
    {
        name:"o9",
        label:"Code postal" 
    },
    {
        name:"l1",
        label:"Téléphone" 
    },
    {
        name:"b4",
        label:"Téléphone 2" 
    },
    {
        name:"co",
        label:"Télécopieur" 
    },
    {
        name:"p2",
        label:"Site web" 
    }
    ],
    TAG : [
    {
        name:"shorttext",
        label:"Short Text"
    },
    {
        name:"longtext",
        label:"Long Text"
    },
    {
        name:"hiddentext",
        label:"Injected Field"
    }
    ],
    CHOICE : [
    {
        name:"dropdown",
        label:"Drop Down (1)"
    },
    {
        name:"radio",
        label:"Radio Buttons (1)"
    },
    {
        name:"check",
        label:"CheckBoxes (mult)"
    }
    ],
    CHOICESET : [
    {
        name:"choiceset",
        label:"Choiceset placeholder"
    }/*,
    {
        name:"breadcrumb",
        label:"Bread Crumb"
    }*/
   ],
    LABEL : [
    {
        name:"Text",
        label:"Text"
    }]
};

// TODO: Validation : shouls be combined ?
eko.validation = [
{
    name:"none",
    label:"None"
},
{
    name:"required",
    label:"Required"
}
];

eko.validators = {
    'none': function(value){
        return [true,'OK'];
    },
    'required': function(value){
        if (isEmpty(value)){
            return [false,'ce champs est requis'];
        }
        return [true,'OK'];
    }

};

eko.voteFrequency = [
{
    name:"",
    label:"Illimited"
},
{
    name:"DAY",
    label:"Once a Day"
},
{
    name:"MONTH",
    label:"Once a month"
},
{
    name:"YEAR",
    label:"Once a year"
}
];

eko.groupsets = {
    "language" : [
    ["Language"],[["en","English"]],[["fr","Francais"]]
    ],

    "sex" : [
    ["Sex"],[["M","Male"]],[["F","Female"]]
    ],

    "sexe" : [
    ["Sexe"],[["M","Homme"]],[["F","Femme"]]
    ],

    "oneLevelWithId": [
    ["One Level With ID"],
    [["value1", "Label 1" ]],
    [["value2", "Label 2" ]],
    [["value3", "Label 3" ]]
    ],

    "twoLevelWithId": [
    ["Two Levels With ID","Second Level"],
    [["grvalue1", "Group Label 1" ],["value1", "Label 1" ]],
    [null,["value2", "Label 2" ]],
    [null,["value3", "Label 3" ]],
    [["grvalue2", "Group Label 2" ],["value4", "Label 4" ]],
    [null,["value5", "Label 5" ]],
    [null,["value6", "Label 6" ]]
    ]
};

