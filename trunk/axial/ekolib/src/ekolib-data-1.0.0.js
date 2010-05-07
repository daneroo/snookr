/* 
 * This contains seed data for testing
 *  -- requires ekolib-constants (or it should)
 */

var contest1 = {
    "ctt_id":"35",
    "accn_id":"316",
    "name":"SARROS Exposant",
    "start_date":"",
    "tpl_id":"1976",
    "vote_frequency":"YEAR",
    "lang":"fr",
    "steps":[{
        "intro":"",
        "fields":[{
            "type":"CHOICESET",
            "subtype":"csssQC",
            "label":"CSSS",
            "validation":"none",
            "options":[]
        },{
            "type":"CHOICESET",
            "subtype":"sexe",
            "label":"Sexe",
            "validation":"none",
            "options":[]
        },{
            "type":"CHOICESET",
            "subtype":"Test4x3x2WL",
            "label":"Test4x3x2WL",
            "validation":"none",
            "options":[]
        },{
            "type":"CHOICE",
            "subtype":"dropdown",
            "label":"Nom de l'établissement",
            "validation":"none",
            "options":[{
                "name":"3391",
                "label":"ASSS du Bas-Saint-Laurent"
            },{
                "name":"3472",
                "label":"ASSS de Chaudière-Appalaches"
            },{
                "name":"3452",
                "label":"ASSS de l'Abitibi-Témiscamingue"
            }/*,{
                "name":"3420",
                "label":"ASSS de l'Estrie"
            },{
                "name":"3446",
                "label":"ASSS de l'Outaouais"
            },{
                "name":"3406",
                "label":"ASSS de la Capitale-Nationale"
            },{
                "name":"3459",
                "label":"ASSS de la Côte-Nord"
            },{
                "name":"3469",
                "label":"ASSS de la Gaspésie - Îles-de-la-Madeleine"
            },{
                "name":"3411",
                "label":"ASSS de la Mauricie et Centre-du-Québec"
            },{
                "name":"3491",
                "label":"ASSS de la Montérégie"
            },{
                "name":"3488",
                "label":"ASSS de Lanaudière"
            },{
                "name":"3478",
                "label":"ASSS de Laval"
            },{
                "name":"3433",
                "label":"ASSS de Montréal"
            },{
                "name":"3480",
                "label":"ASSS des Laurentides"
            },{
                "name":"3399",
                "label":"ASSS du Saguenay - Lac-Saint-Jean"
            },{
                "name":"3405",
                "label":"CSSS de Chicoutimi"
            },{
                "name":"3502",
                "label":"CSSS de la Haute-Yamaska"
            },{
                "name":"3448",
                "label":"CSSS de la Vallée-de-la-Gatineau"
            },{
                "name":"3403",
                "label":"CSSS de Lac-Saint-Jean"
            },{
                "name":"3451",
                "label":"CSSS de Papineau"
            },{
                "name":"3449",
                "label":"CSSS des Collines"
            },{
                "name":"3429",
                "label":"CSSS du Val-Saint-François"
            },{
                "name":"3498",
                "label":"CSSS la Pommeraie"
            },{
                "name":"3437",
                "label":"CSSS Cavendish"
            },{
                "name":"3494",
                "label":"CSSS Champlain"
            },{
                "name":"3401",
                "label":"CSSS Cléophas-Claveau"
            },{
                "name":"3440",
                "label":"CSSS d'Ahuntsic et Montréal-Nord"
            },{
                "name":"3484",
                "label":"CSSS d'Antoine-Labelle"
            },{
                "name":"3487",
                "label":"CSSS d'Argenteuil"
            },{
                "name":"3523",
                "label":"CSSS d'Arthabaska-et-de-L'Érable"
            },{
                "name":"3477",
                "label":"CSSS de Beauce"
            },{
                "name":"3527",
                "label":"CSSS de Bécancour -- Nicolet-Yamaska"
            },{
                "name":"3528",
                "label":"CSSS de Brodeaux-Cartierville -- Saint-Laurent"
            },{
                "name":"3410",
                "label":"CSSS de Charlevoix"
            },{
                "name":"3435",
                "label":"CSSS de Dorval-Lachine-LaSalle"
            },{
                "name":"3450",
                "label":"CSSS de Gatineau"
            },{
                "name":"3402",
                "label":"CSSS de Jonquière"
            },{
                "name":"3393",
                "label":"CSSS de Kamouraska"
            },{
                "name":"3526",
                "label":"CSSS de l'Énergie"
            },{
                "name":"3467",
                "label":"CSSS de l'Hématite"
            },{
                "name":"3434",
                "label":"CSSS de l'ouest-de-l'Île"
            },{
                "name":"3533",
                "label":"CSSS de la Baie-des-Chaleurs"
            },{
                "name":"3468",
                "label":"CSSS de la Bais-James"
            },{
                "name":"3466",
                "label":"CSSS de la Basse-Côte-Nord"
            },{
                "name":"3534",
                "label":"CSSS de La Côte-de-Gaspé"
            },{
                "name":"3460",
                "label":"CSSS de la Haut-Côte-Nord"
            },{
                "name":"3531",
                "label":"CSSS de la Haute-Gaspésie"
            },{
                "name":"3396",
                "label":"CSSS de la Matapédia"
            },{
                "name":"3461",
                "label":"CSSS de la Minganie"
            },{
                "name":"3394",
                "label":"CSSS de la Mitis"
            },{
                "name":"3438",
                "label":"CSSS de la Montagne"
            },{
                "name":"3430",
                "label":"CSSS de la MRC Coaticook"
            },{
                "name":"3445",
                "label":"CSSS de la Pointe-de-l\'Île"
            },{
                "name":"3535",
                "label":"CSSS de la région de Thetford"
            },{
                "name":"3457",
                "label":"CSSS de la Vallée-de-l'Or"
            },{
                "name":"3522",
                "label":"CSSS de la Véllée-de-la-Batiscan"
            },{
                "name":"3408",
                "label":"CSSS de la Vieille-Capitale"
            },{
                "name":"3479",
                "label":"CSSS de Laval"
            },{
                "name":"3521",
                "label":"CSSS de Maskinongé"
            },{
                "name":"3431",
                "label":"CSSS de Memphrémagog"
            },{
                "name":"3475",
                "label":"CSSS de Montmagny-L'Islet"
            },{
                "name":"3407",
                "label":"CSSS de Portneuf"
            },{
                "name":"3409",
                "label":"CSSS de Québec-Nord"
            },{
                "name":"3397",
                "label":"CSSS de Rimouski-Neigette"
            },{
                "name":"3398",
                "label":"CSSS de Rivière-du-Loup"
            },{
                "name":"3456",
                "label":"CSSS de Rouyn-Noranda"
            },{
                "name":"3486",
                "label":"CSSS de Saint-Jérôme"
            },{
                "name":"3443",
                "label":"CSSS de Saint-Léonard et Saint-Michel"
            },{
                "name":"3465",
                "label":"CSSS de Sept-Iles"
            },{
                "name":"3489",
                "label":"CSSS de Sud de Lanaudière"
            },{
                "name":"3530",
                "label":"CSSS de Témiscaming-et-de-Kipawa"
            },{
                "name":"3395",
                "label":"CSSS de Témiscouata"
            },{
                "name":"3482",
                "label":"CSSS de Thérèse-De Blainville"
            },{
                "name":"3524",
                "label":"CSSS de Trois-Rivières"
            },{
                "name":"3500",
                "label":"CSSS de Vaudreuil-Soulanges"
            },{
                "name":"3454",
                "label":"CSSS des Aurores-Boréales"
            },{
                "name":"3392",
                "label":"CSSS des Basques"
            },{
                "name":"3473",
                "label":"CSSS des Etchemins"
            },{
                "name":"3471",
                "label":"CSSS des Îles"
            },{
                "name":"3481",
                "label":"CSSS des Pays-d'en-Haut"
            },{
                "name":"3483",
                "label":"CSSS des Sommets"
            },{
                "name":"3428",
                "label":"CSSS des Sources"
            },{
                "name":"3404",
                "label":"CSSS Domaine-du-Roy"
            },{
                "name":"3525",
                "label":"CSSS Drummond"
            },{
                "name":"3441",
                "label":"CSSS du Coeur-de-l'Île"
            },{
                "name":"3476",
                "label":"CSSS du Grand Littoral"
            },{
                "name":"3432",
                "label":"CSSS du Granit"
            },{
                "name":"3425",
                "label":"CSSS du Haut-Saint-François"
            },{
                "name":"3501",
                "label":"CSSS du Haut-Saint-Laurent"
            },{
                "name":"3520",
                "label":"CSSS du Haut-Saint-Maurice"
            },{
                "name":"3485",
                "label":"CSSS du Lac-des-Deux-Montagnes"
            },{
                "name":"3453",
                "label":"CSSS du Lac-Témismingue"
            },{
                "name":"3490",
                "label":"CSSS du Nord de Lanaudière"
            },{
                "name":"3447",
                "label":"CSSS du Pontiac"
            },{
                "name":"3532",
                "label":"CSSS du Rocher-Percé"
            },{
                "name":"3436",
                "label":"CSSS du Sud-Ouest -- Verdun"
            },{
                "name":"3496",
                "label":"CSSS du Suroît"
            },{
                "name":"3455",
                "label":"CSSS Eskers de l'Abitibi"
            },{
                "name":"3493",
                "label":"CSSS Haut-Richelieu -- Rouville"
            },{
                "name":"3497",
                "label":"CSSS Jardins-Roussillon"
            },{
                "name":"3442",
                "label":"CSSS Jeanne-Mance"
            },{
                "name":"3444",
                "label":"CSSS Lucille-Teasdale"
            },{
                "name":"3464",
                "label":"CSSS Manicouagan"
            },{
                "name":"3400",
                "label":"CSSS Maria-Chapdelaine"
            },{
                "name":"3492",
                "label":"CSSS Pierre-Boucher"
            },{
                "name":"3495",
                "label":"CSSS Pierre-De Saurel"
            },{
                "name":"3462",
                "label":"CSSS Port-Cartier"
            },{
                "name":"3499",
                "label":"CSSS Richelieu-Yamaska"
            }*/,{
                "name":"3529",
                "label":"CSSS Universitaire de gériatrie de Sherbrooke"
            } ]
        },{
            "type":"EKO",
            "subtype":"w2",
            "label":"Adresse",
            "validation":"none",
            "options":[]
        },{
            "type":"EKO",
            "subtype":"nom",
            "label":"Nom",
            "validation":"none",
            "options":[]
        },{
            "type":"EKO",
            "subtype":"o0",
            "label":"Prénom",
            "validation":"none",
            "options":[]
        },{
            "type":"CHOICE",
            "subtype":"radio",
            "label":"Allergie",
            "validation":"none",
            "options":[{
                "name":"3519",
                "label":"Oui"
            },{
                "name":"3519",
                "label":"Non"
            }]
        },{
            "type":"EKO",
            "subtype":"l1",
            "label":"Téléphone",
            "validation":"none",
            "options":[]
        },{
            "type":"EKO",
            "subtype":"co",
            "label":"Télécopieur",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Nom 1",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Prénom 1",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Allergie 1",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Nom 2",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Prénom 2",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Allergie 2",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Nom 3",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Prénom 3",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Allergie 3",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Nom 4",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Prénom 4",
            "validation":"none",
            "options":[]
        },{
            "type":"TAG",
            "subtype":"shorttext",
            "label":"Allergie 4",
            "validation":"none",
            "options":[]
        },{
            "type":"CHOICE",
            "subtype":"radio",
            "label":" Connexion Internet",
            "validation":"none",
            "options":[{
                "name":"3389",
                "label":"Oui"
            },{
                "name":"3389",
                "label":"Non"
            }]
        },{
            "type":"CHOICE",
            "subtype":"radio",
            "label":"Annonce d'un tirage",
            "validation":"none",
            "options":[{
                "name":"3390",
                "label":"Oui"
            },{
                "name":"3390",
                "label":"Non"
            }]
        }]
    }],
    "css_url":"http://axialdev.com/emailing/Sarros/sarros2010.css",
    "submit_translation":"s'inscrire"
};