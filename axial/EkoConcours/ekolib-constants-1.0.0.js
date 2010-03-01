/* 
 * Depends on ekolib-x.x.x.js (for definition of namespace)
 */


eko.contest.FieldTypes = { // and subtypes
    EKO : [
    {
        name:"prenom",
        label:"Prénom"
    },
    {
        name:"nom",
        label:"Nom"
    },
    {
        name:"couriel",
        label:"Couriel"
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
    ]
};
eko.groups = [
{
    name:"gr-french",
    label:"Francais"
},
{
    name:"gr-english",
    label:"Anglais"
},
{
    name:"gr-child",
    label:"Enfant"
},
{
    name:"gr-teen",
    label:"Adolescent"
},
{
    name:"gr-adult",
    label:"Adulte"
},
{
    name:"gr-green",
    label:"Vert"
},
{
    name:"gr-techsavy",
    label:"Techno"
},
{
    name:"gr-bio",
    label:"Bio"
}
];


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
            return [false,'This field is required'];
        }
        return [true,'OK'];
    }

};
