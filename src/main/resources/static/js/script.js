//Panier : modifier une ligne de commande par quantite
function updateLigneDeCommande(ligneDeCommandeId, nouvelleQuantite) {
    // Récupérer le jeton CSRF pour la sécurité
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    $.ajax({
        url: '/api/ligne-de-commande/update', // L'URL de votre endpoint côté serveur
        type: 'POST', // ou 'PUT' selon la configuration de votre serveur
        contentType: 'application/json', // Type de contenu envoyé
        data: JSON.stringify({
            ligneDeCommandeId: ligneDeCommandeId,
            quantite: nouvelleQuantite
            // Pas besoin d'envoyer le total, il peut être recalculé côté serveur
        }),
        beforeSend: function(xhr) {
            // Ajouter le jeton CSRF dans les en-têtes de la requête pour la sécurité
            xhr.setRequestHeader(csrfHeader, csrfToken);
        },
        success: function(response) {
            console.log("Ligne de commande mise à jour avec succès 'Test totale':", response);
            // Mettre à jour l'interface utilisateur en conséquence
            // Par exemple, mettez à jour le total affiché pour cette ligne de commande
            // et potentiellement le total global du panier si nécessaire
            $("#total-ligne-" + ligneDeCommandeId).text(response.total + ' €');
            updateCartTotal(); // Mettez à jour le total du panier si cette fonction est définie
        },
        error: function(xhr, status, error) {
            console.error("Erreur lors de la mise à jour de la ligne de commande:", error);
            // Gérez l'erreur, par exemple, en affichant un message à l'utilisateur
        }
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
    var totalPanier = 0.0;

    $.ajax({
        type: "GET",
        url: "/api/" + panierIdValue + "/lignesdecommande",
        dataType: "json",
        success: function (data) {
            $("#cart-items").empty();
            if (data.length === 0) {
                $("#cart-items").append('<tr><td colspan="4">Votre panier est vide7778888.</td></tr>');
            } else {
                $.each(data, function (index, ligneDeCommande) {
                    var montantTotal = ligneDeCommande.quantite * ligneDeCommande.prixUnitaire;
                    totalPanier += montantTotal;
                    var newRow = '<tr>' +
                        '<td>' + ligneDeCommande.nomProduit + '</td>' +
                        '<td>' + ligneDeCommande.prixUnitaire.toFixed(2) + ' €</td>' +
                        '<td>' + ligneDeCommande.quantite + '</td>' +
                        '<td>' + montantTotal.toFixed(2) + ' €</td>' +
                        '</tr>';
                    $("#cart-items").append(newRow);
                });
            }
            // Mettre à jour le total du panier
            $("#panier-total").text(totalPanier.toFixed(2));
        },
        error: function (jqXHR, textStatus, errorThrown) {
            console.error("Erreur de requête Ajax: " + textStatus + ", " + errorThrown);
            alert("Une erreur est survenue lors de la récupération des lignes de commande du panier.");
        }
    });
}



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

    //alert("Données envoyées :\nProduit ID: " + produitIdValue + "\nQuantité: " + quantite + "\nPanier ID: " + panierId);

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
            getListLigneDeCommandePanier(panierId);

            // Rafraîchir la page uniquement pour le premier produit ajouté
            // Vérifiez si le panier était initialement vide et si c'est le cas, rechargez la page
            if ($("#cart-items").children().length === 0) {
                location.reload(); // Rafraîchit la page actuelle
            } else {
                getListLigneDeCommandePanier(panierId);
            }
        },
        error: function (error) {
            console.error('Erreur lors de l\'ajout de la ligne de commande :', error);
            // Réalisez les actions nécessaires en cas d'erreur
            alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
        }
    });

    // Réinitialiser la quantité à 1
    quantiteInput.val(1);
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
             // Afficher un message d'erreur à l'utilisateur
             alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
        }
    });
}

$(document).ready(function () {
    /**
    $('#cart-items').on('change', '.quantity-input', function() {
       var quantity = parseInt($(this).val());
       var price = parseFloat($(this).closest('tr').find('#prix-produit').text());
       var total = quantity * price;
       $(this).closest('tr').find('#total-produit').text(total.toFixed(2));
       updateCartTotal();
    });
    **/



    //Avoir la quantite d'un produit (version modifiée pour retourner une promesse)
    // Fonction modifiée pour définir l'attribut 'max' de l'input
    function getQuantiteProduit(produitId) {
        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");
        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            type: "GET",
            url: "/quantite-produit/" + produitId,
            dataType: "json",
            headers: headers,
            success: function (quantite) {
                // Définir l'attribut max avec la quantité du stock
                $('#quantite_' + produitId).attr('max', quantite);
            },
            error: function (error) {
                console.error('Erreur lors de la récupération de la quantité du produit :', error);
            }
        });
    }

    // Appel pour chaque produit à la page chargement
        $('.quantity-input').each(function() {
            var productId = $(this).data('product-id');
            getQuantiteProduit(productId);
        });

        // Surveiller les changements sur les entrées de quantité
        $('.quantity-input').on('change', function() {
            var productId = $(this).data('product-id');
            var maxQuantite = parseInt($(this).attr('max'));
            var currentQuantite = parseInt($(this).val());

            // Vérifier si la quantité dépasse le stock maximum
            if(currentQuantite > maxQuantite) {
                // Afficher un message d'erreur
                $('#error_' + productId).show();
                $(this).val(maxQuantite); // Réinitialiser à la quantité max
            } else {
                // Cacher le message d'erreur si la quantité est valide
                $('#error_' + productId).hide();
            }
        });

    $('#cart-items').on('change', '.quantity-input', function() {
        var ligneDeCommandeId = $(this).closest('tr').data('ligne-commande-id'); // Récupère l'ID de la ligne de commande
        var quantity = parseInt($(this).val());
        var price = parseFloat($(this).closest('tr').find('#prix-produit').text());
        var total = quantity * price;

        // Met à jour l'affichage du total pour cette ligne de commande dans le tableau
        $(this).closest('tr').find('#total-produit').text(total.toFixed(2));

        // Appel AJAX pour mettre à jour la quantité et potentiellement le total côté serveur
        updateLigneDeCommande(ligneDeCommandeId, quantity);

        // Met à jour le total du panier
        updateCartTotal();
    });

    function updateCartTotal() {
       var total = 0;
       $('.product-total').each(function() {
          total += parseFloat($(this).text());
       });
       $('#panier-total').text(total.toFixed(2));
    }

    // Modal panier pour la confirmation de suppression d'une ligne de commande
    document.addEventListener("DOMContentLoaded", function() {
        var myModalEl = document.getElementById('exampleModalCenter');
        if (myModalEl) {
            var myModal = new bootstrap.Modal(myModalEl);

            // Utilisez querySelectorAll et un boucle si vous avez plusieurs boutons fermer
            document.querySelectorAll('.btn-close').forEach(function(btnClose) {
                btnClose.addEventListener('click', function() {
                    alert("testModal");
                    myModal.hide();
                });
            });
        }
    });


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