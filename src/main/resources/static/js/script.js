//Avoir la quantite d'un produit
function getQuantiteProduit(produitId) {
    // Récupérez le jeton CSRF depuis les balises meta
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        // Créez un objet d'en-tête avec le jeton CSRF
        var headers = {};
        headers[csrfParameterName] = csrfToken;
    $.ajax({
        type: "GET",
        url: "/quantite-produit/" + produitId,
        dataType: "json",
        headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
        success: function (quantite) {
            // Faites quelque chose avec la quantité, par exemple, l'afficher
            console.log("Quantité du produit #" + produitId + ": " + quantite);
            return quantite;
        },
        error: function (error) {
            console.error('Erreur lors de la récupération de la quantité du produit :', error);
        }
    });
}

// Avoir la quantité d'un produit
function getQuantiteProduit(produitId) {
    return new Promise(function (resolve, reject) {
        // Récupérez le jeton CSRF depuis les balises meta
                var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
                var csrfToken = $("meta[name='_csrf.token']").attr("content");

                // Créez un objet d'en-tête avec le jeton CSRF
                var headers = {};
                headers[csrfParameterName] = csrfToken;
        $.ajax({
            type: "GET",
            url: "/quantite-produit/" + produitId,
            dataType: "json",
            headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
            success: function (quantite) {
                // Résoudre la promesse avec la quantité
                resolve(quantite);
            },
            error: function (error) {
                // Rejeter la promesse en cas d'erreur
                reject(error);
            }
        });
    });
}


//Affiche la liste des produits actif et quantite >= 1
function getProduitActif() {
    var addedProductIds = new Set(); // Ensemble pour stocker les IDs des produits déjà ajoutés
    var listeProduits = $('#liste-produits');


    $.ajax({
        type: "GET",
        url: "/produits",
        dataType: "json",
        success: function (data) {
            listeProduits.empty();

            if (data.length === 0) {
                // Si la liste est vide, affichez un message "Pas de produit"
                listeProduits.append('<p>Pas de produit</p>');
            } else {
                data.forEach(function (produit) {
                    // Vérifiez si le produit a déjà été ajouté
                    if (!addedProductIds.has(produit.id)) {
                        addedProductIds.add(produit.id);

                        //<-- !!!!!!!!! faire une fonction qui récupération de idPanier -->
                        var productHtml = `
                            <div class="col-md-3 mb-4">
                            <div class="card product-card">
                                <input type="hidden" id="produitId-${produit.id}" name="produitId" value="${produit.id}">
                                <input type="hidden" id="panierId" name="panierId" value="1">
                                <img class="card-img-top product-image" src="/display?id=${produit.id}" alt="Image du produit">
                                <div class="card-body product-details" style="padding-bottom:0;padding-top:0;">
                                    <h2 class="product-title">${produit.nom}</h2>
                                    <div class="rating-container">
                                        <div class="rating">
                                            <!-- Étoiles ici... -->
                                            <div class="rating">
                                                <!-- Étoile 1 -->
                                                <i class="${produit.cote >= 1 ? 'fas fa-star' : (produit.cote > 0 && produit.cote < 1) ? 'fas fa-star-half-alt' : 'far fa-star'}"></i>
                                                <!-- Étoile 2 -->
                                                <i class="${produit.cote >= 2 ? 'fas fa-star' : (produit.cote > 1 && produit.cote < 2) ? 'fas fa-star-half-alt' : 'far fa-star'}"></i>
                                                <!-- Étoile 3 -->
                                                <i class="${produit.cote >= 3 ? 'fas fa-star' : (produit.cote > 2 && produit.cote < 3) ? 'fas fa-star-half-alt' : 'far fa-star'}"></i>
                                                <!-- Étoile 4 -->
                                                <i class="${produit.cote >= 4 ? 'fas fa-star' : (produit.cote > 3 && produit.cote < 4) ? 'fas fa-star-half-alt' : 'far fa-star'}"></i>
                                                <!-- Étoile 5 -->
                                                <i class="${produit.cote == 5 ? 'fas fa-star' : (produit.cote > 4 && produit.cote < 5) ? 'fas fa-star-half-alt' : 'far fa-star'}"></i>
                                            </div>
                                        </div>
                                        <div>
                                            <p>
                                                ${produit.cote}
                                                (nb x)
                                            </p>
                                        </div>
                                    </div>
                                    <h4 class="product-price">${produit.prix} €</h4>
                                    <div class="form-group mb-3" style="display: flex;">
                                        <div class="input-group input-spinner">
                                            <div style="display: flex;">
                                                <div class="input-group-prepend">
                                                    <button class="button-minusJs btn btn-sm btn-outline-success" type="button" data-field="quantity">
                                                        <i class="fa-solid fa-minus"></i>
                                                    </button>
                                                </div>
                                                <input type="hidden" id="quantite-${produit.id}" value="1" class="quantity-input">
                                                <input style="width: 80px; margin: 0;" type="number" step="1" min="1" max="" id="quantite-${produit.id}" name="quantite" class="form-control form-control-sm form-input text-center quantity-input">
                                                <div class="input-group-append">
                                                    <button class="button-plusJs btn btn-sm btn-outline-success" type="button" data-field="quantity">
                                                        <i class="fa-solid fa-plus"></i>
                                                    </button>
                                                </div>
                                            </div>
                                        </div>

                                        <div>
                                            <button style="background-color: #cccfce;" title="Ajouter aux favoris">
                                                <i class="fa-regular fa-heart" style="color: #d91717;"></i>
                                                <i class="fa-solid fa-heart" style="color: #d91717;"></i>
                                            </button>
                                        </div>
                                    </div>
                                    <button class="btn btn-success add-to-cart-button" onclick="addToCart(${produit.id})" data-productId="${produit.id}" >Ajouter au panier</button>
                                </div>
                            </div>
                            </div>
                        `;
                        listeProduits.append(productHtml);
                        addedProductIds.add(produit.id);
                    }
                });
            }
        },
        error: function (error) {
            console.error('Erreur lors de la récupération des produits :', error);
        }
    });
}


function getProduitDetails2(produitId) {
    // Convertir produitId en un Long
    var produitIdValue = parseInt(produitId);

    $.ajax({
        url: '/produit/api/' + produitIdValue, // URL pour récupérer les détails du produit
        type: 'GET',
        dataType: 'json',
        beforeSend: function () {
            // Afficher l'indicateur de chargement pendant la requête AJAX (si nécessaire)
            // Vous pouvez ajouter votre propre indication de chargement ici
        },
        success: function (data) {
            // La réponse a été reçue avec succès
            console.log('Produit récupéré avec succès :', data);

            // Maintenant, vous avez les détails du produit dans l'objet 'data'
            // Vous pouvez traiter ces détails comme vous le souhaitez ici

            // Exemple : Afficher le nom et le prix du produit
            /**var nom = data.nom;
            var prix = parseFloat(data.prix);
            console.log('Nom du produit :', nom);
            console.log('Prix du produit :', prix);**/

           return data;

            // Effectuez d'autres actions nécessaires avec les détails du produit
        },
        error: function (error) {
            // Une erreur s'est produite lors de la requête AJAX pour récupérer les détails du produit
            console.error('Erreur lors de la récupération du produit :', error);

            // Vous pouvez gérer les erreurs ici, par exemple, afficher un message d'erreur à l'utilisateur
        }
    });
}




// Fonction pour récupérer le panier en spécifiant l'ID du panier
function getPanier(idPanier) {
    var idPanierval = parseInt(idPanier);

    // Récupérez le jeton CSRF depuis les balises meta
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        // Créez un objet d'en-tête avec le jeton CSRF
        var headers = {};
        headers[csrfParameterName] = csrfToken;

    $.ajax({
        url: '/panier/api/' + idPanierval, // Utilisez l'ID du panier spécifié
        type: 'GET',
        dataType: 'json',
        headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
        success: function (panierData) {
            // La réponse a été reçue avec succès
            console.log('Panier récupéré avec succès :', panierData);
            //getListLigneDeCommandePanier(idPanierval);
        },
        error: function (xhr, status, error) {
            console.error('Erreur AJAX :', error);
        }
    });
}

function getListLigneDeCommandePanier(panierId) {
    var panierIdValue = parseInt(panierId);
    var totalPanier = 0.0; // Variable pour stocker le total du panier

     // Récupérez le jeton CSRF depuis les balises meta
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        // Créez un objet d'en-tête avec le jeton CSRF
        var headers = {};
        headers[csrfParameterName] = csrfToken;

    $.ajax({
        type: "GET",
        url: "/api/" + panierIdValue + "/lignesdecommande",
        dataType: "json",
        headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
        success: function (data) {
            // Effacer le contenu de l'élément #cart-items
            $("#cart-items").empty();

            // Créez une nouvelle ligne pour chaque élément de data
            $.each(data, function (index, ligneDeCommande) {
                var montantTotal = ligneDeCommande.quantite * ligneDeCommande.prixUnitaire;
                totalPanier += montantTotal; // Ajoutez le montant total au total du panier

                var newRow = '<tr>' +
                    '<td>' + ligneDeCommande.nomProduit + '</td>' +
                    '<td>' + ligneDeCommande.prixUnitaire.toFixed(2) + ' €</td>' +
                    '<td>' + ligneDeCommande.quantite + '</td>' +
                    '<td>' + ligneDeCommande.montantTotal.toFixed(2) + ' €</td>' +
                    '</tr>';
                // Ajouter la ligne au panier
                $("#cart-items").append(newRow);
            });

            // Ajoutez une ligne pour afficher le total du panier centrée horizontalement
            var totalRow = '<tr>' +
                '<td colspan="4" style="text-align: center;"><strong>Total : ' + totalPanier.toFixed(2) + ' €</strong></td>' +
                '</tr>';
            $("#cart-items").append(totalRow);

            // Ajoutez un bouton "Voir panier"
            var voirPanierButton = '<button class="btn btn-primary" onclick="confirmerPanier()"><i class="bi bi-cart-check"></i> Confirmer le panier</button>';
            $("#cart-items").append('<tr><td colspan="4">' + voirPanierButton + '</td></tr>');
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erreur de requête Ajax: " + errorThrown);
        }
    });
}
/**
function addLigneDeCommandeToPanier(produitId, quantite, panierId) {
    // Créez un objet contenant les données à envoyer
    var data = {
        produitId: produitId,
        quantite: quantite,
        panierId: panierId
    };

    alert("produiID : "+produitId + " Qty:"+ quantite + " panierId:" + panierId);

    // Récupérez le jeton CSRF depuis les balises meta
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        // Créez un objet d'en-tête avec le jeton CSRF
        var headers = {};
        headers[csrfParameterName] = csrfToken;

    // Effectuez la requête Ajax POST
    $.ajax({
        url: '/addToCart', // Remplacez par l'URL correcte pour votre endpoint
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
        success: function (response) {
            // La requête a réussi, vous pouvez traiter la réponse ici
            console.log('Ligne de commande ajoutée avec succès:', response);
            //getProduitDetails(produitId);
            // Effacer le contenu de l'élément #cart-items
            // Réalisez les actions nécessaires en cas de succès, par exemple, mettre à jour l'affichage du panier
            // ou afficher un message de succès à l'utilisateur.
        },
        error: function (error) {
            // Une erreur s'est produite lors de la requête Ajax
            console.error('Erreur lors de l\'ajout de la ligne de commande:', error);

            // Réalisez les actions nécessaires en cas d'erreur, par exemple, afficher un message d'erreur à l'utilisateur.
        }
    });
}

function addToCart(produitId) {
    // Convertir produitId en un Long
    var produitIdValue = parseInt(produitId);

    // Obtenir la quantité depuis l'élément input à l'intérieur de la même div
    var quantiteInput = $("#quantite-" + produitIdValue);
    var quantite = parseInt(quantiteInput.val());
    var panierId = $("#panierId").val();
    //alert("qty:" + quantite + ":" + panierId + "produitId:" + produitId + "panier id: " + panierId);

    // Appeler la fonction pour ajouter la ligne de commande au panier
    addLigneDeCommandeToPanier(produitIdValue, quantite, panierId);

    // Réinitialiser la quantité à 1
    quantiteInput.val(1);

    // Affiche le panier actuel
    getListLigneDeCommandePanier(panierId);
}**/



/**
function addOrUpdateLigneDeCommande(produitId, quantite, panierId) {
    // Créez un objet contenant les données à envoyer
    var data = {
        produitId: produitId,
        quantite: quantite,
        panierId: panierId
    };

    // Récupérez le jeton CSRF depuis les balises meta
    var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
    var csrfToken = $("meta[name='_csrf.token']").attr("content");

    // Créez un objet d'en-tête avec le jeton CSRF
    var headers = {};
    headers[csrfParameterName] = csrfToken;

    // Effectuez la requête Ajax POST
    $.ajax({
        url: '/addToCart', // Remplacez par l'URL correcte pour votre endpoint
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
        success: function (response) {
            // La requête a réussi, vous pouvez traiter la réponse ici
            console.log('Ligne de commande ajoutée avec succès:', response);
            //getProduitDetails(produitId);
            // Effacer le contenu de l'élément #cart-items
            // Réalisez les actions nécessaires en cas de succès, par exemple, mettre à jour l'affichage du panier
            // ou afficher un message de succès à l'utilisateur.
        },
        error: function (error) {
            // Une erreur s'est produite lors de la requête Ajax
            console.error('Erreur lors de l\'ajout de la ligne de commande:', error);

            // Réalisez les actions nécessaires en cas d'erreur, par exemple, afficher un message d'erreur à l'utilisateur.
        }
    });
}

// Utilisation de la fonction combinée
function addToCart(produitId) {
    // Convertir produitId en un Long
    var produitIdValue = parseInt(produitId);

    // Obtenir la quantité depuis l'élément input à l'intérieur de la même div
    var quantiteInput = $("#quantite-" + produitIdValue);
    var quantite = parseInt(quantiteInput.val());
    var panierId = $("#panierId").val();

    // Appeler la fonction pour ajouter ou mettre à jour la ligne de commande au panier
    addOrUpdateLigneDeCommande(produitIdValue, quantite, panierId);

    // Réinitialiser la quantité à 1
    quantiteInput.val(1);

    // Affiche le panier actuel
    getListLigneDeCommandePanier(panierId);
}**/

function addToCart(produitId) {
    // Vérifiez si produitId peut être converti en un entier
    var produitIdValue = parseInt(produitId);
    if (isNaN(produitIdValue)) {
        console.error('Erreur : produitId invalide');
        return;
    }

    // Obtenez la quantité depuis l'élément input dans la même div
    var quantiteInput = $("#quantite-" + produitIdValue);
    var quantite = parseInt(quantiteInput.val());
    var panierId = $("#panierId").val();

    // Récupérez le jeton CSRF depuis les balises meta
    var csrfToken = $("meta[name='_csrf']").attr("content");

    // Créez un objet contenant les données à envoyer
    var data = {
        produitId: produitIdValue,
        quantite: quantite,
        panierId: panierId
    };

    // Effectuez la requête Ajax POST
    $.ajax({
        url: '/addToCart',
        type: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(data),
        beforeSend: function(xhr) {
            // Ajoutez le jeton CSRF aux en-têtes de la requête
            xhr.setRequestHeader('X-XSRF-TOKEN', csrfToken);
        },
        success: function (response) {
            console.log('Ligne de commande ajoutée avec succès :', response);
            // Réalisez les actions nécessaires en cas de succès
        },
        error: function (error) {
            console.error('Erreur lors de l\'ajout de la ligne de commande :', error);
            // Réalisez les actions nécessaires en cas d'erreur
        }
    });

    // Réinitialiser la quantité à 1
    quantiteInput.val(1);

    // Affiche le panier actuel
    getListLigneDeCommandePanier(panierId);
}





// Fonction pour obtenir les détails du produit, y compris la quantité mise à jour

function getProduitDetails(produitId) {
    // Convertir produitId en un Long
    var produitIdValue = parseInt(produitId);

    // Récupérer le jeton CSRF depuis les balises meta
    var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
    var csrfToken = $("meta[name='_csrf.token']").attr("content");

    // Créer un objet d'en-tête avec le jeton CSRF
    var headers = {};
    headers[csrfParameterName] = csrfToken;

    $.ajax({
        type: "GET",
        url: "/produit/api/" + produitIdValue,
        dataType: "json",
        headers: headers, // Ajoutez le jeton CSRF dans les en-têtes de la requête
        success: function (data) {
            var nouvelleQuantite = data.quantite; // Assurez-vous que la propriété "quantite" correspond à la nouvelle quantité

            // Mettez à jour la valeur maximale (max) de l'élément input avec la nouvelle quantité
            $("#quantite-" + produitId).attr('max', nouvelleQuantite);
        },
        error: function (error) {
            console.error('Erreur lors de la récupération des détails du produit :', error);
        }
    });
}


$(document).ready(function () {
    // Gestionnaire d'événement pour le bouton "-"
    $('.button-minusJs').on('click', function () {
        var productId = $(this).data('product-id');
        var input = $(`#quantite-${productId}`);
        var value = parseInt(input.val());
        var minValue = parseInt(input.attr('min'));
        if (value > minValue) {
            input.val(value - 1);
            alert(`Bouton "-" cliqué pour le produit #${productId}. Nouvelle valeur : ${value - 1}`);
        } else {
            alert(`Bouton "-" désactivé pour le produit #${productId}. La valeur minimale est atteinte.`);
        }
    });

    // Gestionnaire d'événement pour le bouton "+"
    $('.button-plusJs').on('click', function () {
        var productId = $(this).data('product-id');
        var input = $(`#quantite-${productId}`);
        var value = parseInt(input.val());
        var maxValue = parseInt(input.attr('max'));
        if (value < maxValue) {
            input.val(value + 1);
            alert(`Bouton "+" cliqué pour le produit #${productId}. Nouvelle valeur : ${value + 1}`);
        } else {
            alert(`Bouton "+" désactivé pour le produit #${productId}. La valeur maximale est atteinte.`);
        }
    });
});


// Gestionnaire d'événement pour le bouton "-"
    $('.button-minusJs').on('click', function () {
        var productId = $(this).data('product-id');
        var input = $(`#quantite-${productId}`);
        var value = parseInt(input.val());
        var minValue = parseInt(input.attr('min'));
        if (value > minValue) {
            input.val(value - 1);
        }
    });

    // Gestionnaire d'événement pour le bouton "+"
    $('.button-plusJs').on('click', function () {
        var productId = $(this).data('product-id');
        var input = $(`#quantite-${productId}`);
        var value = parseInt(input.val());
        var maxValue = parseInt(input.attr('max'));
        if (value < maxValue) {
            input.val(value + 1);
        }
    });

// Sélectionnez tous les boutons "-" et ajoutez-leur des écouteurs d'événements
    $('.button-minus').click(function () {
        var input = $(this).closest('.input-group').find('input.form-input');
        var value = parseInt(input.val());
        var minValue = parseInt(input.attr('min'));
        if (value > minValue) {
            input.val(value - 1);
        }
    });

    // Sélectionnez tous les boutons "+" et ajoutez-leur des écouteurs d'événements
    $('.button-plus').click(function () {
        var input = $(this).closest('.input-group').find('input.form-input');
        var value = parseInt(input.val());
        var maxValue = parseInt(input.attr('max'));
        if (value < maxValue) {
            input.val(value + 1);
        }
    });