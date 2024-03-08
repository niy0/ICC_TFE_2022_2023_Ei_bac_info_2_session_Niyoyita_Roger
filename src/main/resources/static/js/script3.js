
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

$(document).ready(function () {
    // Fonction pour mettre à jour une ligne de commande
    function updateLigneDeCommande(ligneDeCommandeId, nouvelleQuantite) {
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var csrfHeader = $("meta[name='_csrf_header']").attr("content");

        // S'assurer que les valeurs sont bien récupérées
        //console.log("CSRF Token:", csrfToken, "CSRF Header:", csrfHeader ,"vals :", ligneDeCommandeId, nouvelleQuantite);

        $.ajax({
            url: '/lignedecommande/update',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                id: ligneDeCommandeId,
                quantite: nouvelleQuantite
            }),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(response) {
                console.log("Ligne de commande mise à jour avec succès:", response);
                $("#total-ligne-" + ligneDeCommandeId).text(response.total + ' €');
                updateCartTotal();
            },
            error: function(xhr, status, error) {
                console.error("Erreur lors de la mise à jour de la ligne de commande:", error);
            }
        });
    }



    // Fonction pour mettre à jour le total du panier
    function updateCartTotal() {
        var total = 0;
        $('.product-total').each(function() {
            total += parseFloat($(this).text());
        });
        $('#panier-total').text(total.toFixed(2) + ' €');
    }

    // Fonction pour ajouter un produit au panier
    function addToCart(produitId, quantite) {
        var csrfToken = $("meta[name='_csrf']").attr("content");

        $.ajax({
            url: '/addToCart',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ produitId: produitId, quantite: quantite }),
            beforeSend: function(xhr) {
                xhr.setRequestHeader('X-CSRF-TOKEN', csrfToken);
            },
            success: function(response) {
                console.log('Produit ajouté avec succès:', response);
                // Ajoutez ici la logique pour mettre à jour l'affichage du panier ou rediriger l'utilisateur
            },
            error: function(error) {
                console.error('Erreur lors de l\'ajout au panier:', error);
            }
        });
    }

    // Gestionnaire d'événements pour les boutons "+" et "-"
    $('.button-minusJs, .button-plusJs').click(function () {
        var quantiteInput = $(this).closest('.input-group').find('.quantity-input');
        var quantite = parseInt(quantiteInput.val());
        var productId = quantiteInput.data('product-id');

        if ($(this).hasClass('button-minusJs') && quantite > 1) {
            quantiteInput.val(quantite - 1);
        } else if ($(this).hasClass('button-plusJs')) {
            quantiteInput.val(quantite + 1);
        }

        // Optionnel : Mettez à jour la ligne de commande si nécessaire
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
    // Lorsque la valeur d'un input de quantité change
        $('.quantity-input').on('change', function() {
            // Récupère l'ID du produit à partir de l'attribut de données de l'input
            var productId = $(this).data('product-id');
            // Récupère la nouvelle valeur de quantité
            var newQuantity = $(this).val();

            // Trouve la balise <p> correspondante en utilisant l'ID du produit
            var quantiteText = $('.quantite-text[data-product-id="' + productId + '"] span');

            // Met à jour le texte de la balise <p> avec la nouvelle quantité
            quantiteText.text(newQuantity);
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

    // Fonction pour initialiser les quantités max des produits basées sur le stock
    function initialiserQuantitesMax() {
        $('.quantity-input').each(function() {
            var produitId = $(this).data('product-id');
            getQuantiteProduit(produitId, $(this));
        });
    }

    // Fonction pour obtenir la quantité maximale d'un produit
    function getQuantiteProduit(produitId, inputElement) {
        var csrfToken = $("meta[name='_csrf']").attr("content");
        var headers = { "X-CSRF-TOKEN": csrfToken };

        $.ajax({
            type: "GET",
            url: "/quantite-produit/" + produitId,
            headers: headers,
            success: function(quantite) {
                inputElement.attr('max', quantite);
            },
            error: function(error) {
                console.error('Erreur lors de la récupération de la quantité du produit:', error);
            }
        });
    }

    initialiserQuantitesMax();

    // Autres fonctions nécessaires (si vous en avez ajouté d'autres qui doivent être incluses)



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
});
