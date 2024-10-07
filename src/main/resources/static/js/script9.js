$(document).ready(function () {
    var csrfToken = $("meta[name='_csrf']").attr("content");
    var csrfHeader = $("meta[name='_csrf_header']").attr("content");

    // Lorsque l'utilisateur clique sur le bouton "Ajouter aux favoris"
    $(document).on('click', '.favorite-btn', function() {
        var produitId = $(this).closest('.product-card').find('input[name="produitId"]').val();

        // Si le produitId n'est pas trouvé dans l'input hidden, récupérer via data-attribute
        if (!produitId) {
            produitId = $(this).data('product-id');
        }

        // Récupération de l'ID utilisateur
        var userId = $('#userId').val();

        if (userId) {
            // Envoi des données pour l'ajout aux favoris si l'utilisateur est connecté
            ajouterAuxFavoris(produitId, userId, $(this));  // Passer le bouton actuel pour mise à jour
        } else {
            // Affichage du modal d'erreur si l'utilisateur n'est pas connecté
            var errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
            errorModal.show();
        }
    });

    function ajouterAuxFavoris(produitId, userId, button) {
        // Créer l'objet de données à envoyer
        var data = {
            produitId: produitId,
            userId: userId
        };

        // Envoi de la requête AJAX pour ajouter aux favoris
        $.ajax({
            url: '/user/add/favoris', // Vérifiez que l'URL est correcte
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken); // Ajout des en-têtes CSRF
            },
            success: function(response) {
                // Affichage du modal de succès après l'ajout aux favoris
                var successModal = new bootstrap.Modal(document.getElementById('successModal'));
                successModal.show();

                // Mettre à jour dynamiquement le bouton favoris
                button.removeClass('btn-outline-danger').addClass('btn-danger'); // Changer le style du bouton

                // Changer l'icône de cœur dynamiquement
                var icon = button.find('i'); // Sélectionner l'icône à l'intérieur du bouton
                icon.removeClass('far fa-heart').addClass('fas fa-heart text-white'); // Remplacer l'icône vide par l'icône pleine
            },
            error: function(error) {
                console.error('Erreur lors de l\'ajout aux favoris:', error);
                // Afficher le modal d'erreur si l'ajout échoue
                var errorModal = new bootstrap.Modal(document.getElementById('errorModal'));
                errorModal.show();
            }
        });
    }

    // Fonction pour mettre à jour l'affichage des étoiles en fonction de la valeur sélectionnée
    function updateStars(rating) {
        $(".stars .star").each(function () {
            var starValue = $(this).data("value");
            if (starValue <= rating) {
                $(this).find("i").addClass("fas").removeClass("far");
            } else {
                $(this).find("i").addClass("far").removeClass("fas");
            }
        });
    }

    // Initialiser les étoiles en fonction de la note du produit
    var initialRating = $("#ratingSelect").val();
    updateStars(initialRating);

    // Mettre à jour les étoiles lorsque l'utilisateur change la valeur dans le select
    $("#ratingSelect").on("change", function () {
        var selectedRating = $(this).val();
        updateStars(selectedRating);
    });

    // Mettre à jour la valeur du select lorsque l'utilisateur clique sur une étoile
    $(".stars .star").on("click", function () {
        var clickedRating = $(this).data("value");
        $("#ratingSelect").val(clickedRating);
        updateStars(clickedRating);
    });

    // Lorsque l'utilisateur clique sur le bouton "Noter le produit"
    $('#noterProduitBtn').on('click', function() {
        var produitId = $('input[name="produitId"]').val();
        var userId = $('#userId').val();  // Récupérer l'utilisateur connecté
        var rating = $('#ratingSelect').val();  // Récupérer la note

        if (userId) {
            // Si l'utilisateur est connecté, envoyer la note
            noterProduit(produitId, userId, rating);
        } else {
            // Si l'utilisateur n'est pas connecté, afficher le modal de connexion requise
            var errorModalNotation = new bootstrap.Modal(document.getElementById('errorModalNotation'));
            errorModalNotation.show();
        }
    });


    // Fonction pour envoyer la note du produit via AJAX
    function noterProduit(produitId, userId, rating) {
        $.ajax({
            url: '/produit/' + produitId + '/noter', // URL d'envoi de la note
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ note: rating }),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken); // Ajout du token CSRF
            },
            success: function(response) {
                // Afficher le modal de succès si la note a été envoyée avec succès
                var successModalNotation = new bootstrap.Modal(document.getElementById('successModalNotation'));
                successModalNotation.show();
            },
            error: function(error) {
                console.error('Erreur lors de la soumission de la note:', error);
                alert('Une erreur est survenue lors de l\'envoi de la note.'); // Vous pouvez remplacer par un modal si nécessaire
            }
        });
    }

    // Fonction pour incrémenter les vues
    function incrementerVues(event, produitId) {
        event.preventDefault(); // Empêche la redirection immédiate

        // Requête fetch pour incrémenter les vues
        fetch(`/produit/incrementerVues/${produitId}`, {
            method: 'GET',
        })
        .then(response => response.json()) // Analyser la réponse JSON
        .then(data => {
            if (data === true) {
                console.log('Vue incrémentée avec succès');
            } else {
                console.error('Erreur : produit non trouvé');
            }
            // Rediriger après la réponse, succès ou non
            window.location.href = `/produit/${produitId}`;
        })
        .catch(error => {
            console.error('Erreur dans la requête', error);
            // Rediriger même en cas d'erreur
            window.location.href = `/produit/${produitId}`;
        });
    }

    // Associer la fonction au bouton avec l'attribut `onclick`
    $(document).on('click', 'a[id^="view-product-"]', function (event) {
        event.preventDefault(); // Empêche le comportement par défaut du lien
        var produitId = $(this).data('produit-id'); // Récupère l'ID du produit à partir de l'attribut data-produit-id

        console.log('Produit ID:', produitId); // Vérifier si l'ID est correct

        if (produitId) {
            incrementerVues(event, produitId); // Incrémente les vues si l'ID est valide
        } else {
            console.error("Erreur : l'ID du produit est indéfini.");
        }
    });

    // Calcule des frais de livraison
    function calculateDeliveryFees() {
        var methodCommande = $('#methodCommande').val();
        var totalPanier = parseFloat($('#panier-total').text().replace(' €', ''));

        // Si la méthode de commande est "PICKUP", les frais de livraison sont à 0 €
        if (methodCommande === 'PICKUP') {
            $('#frais-livraison').text('0 ');
            $('#total-commande').text(totalPanier.toFixed(2) );
        }
        // Si la méthode de commande est "DELIVERY", effectuer le calcul des frais
        else if (methodCommande === 'DELIVERY') {
            var rue = $('#rue').val();
            var numero = $('#numero').val();
            var localite = $('#localite').val();
            var ville = $('#ville').val();
            var codePostal = $('#codePostal').val();
            var pays = $('#pays').val();

            if (rue && numero && ville && codePostal && pays) {
                var fullAddress = {
                    rue: rue,
                    numero: numero,
                    localite: localite,
                    ville: ville,
                    codePostal: codePostal,
                    pays: pays
                };

                $.ajax({
                    url: '/api/livraison/calculate',
                    type: 'POST',
                    contentType: 'application/json',
                    data: JSON.stringify(fullAddress),
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader(csrfHeader, csrfToken);
                    },
                    success: function (response) {
                        // Mise à jour du montant des frais de livraison
                        $('#frais-livraison').text(response.fraisLivraison);

                        // Mise à jour du total de la commande (montant des produits + frais de livraison)
                        var fraisLivraison = parseFloat(response.fraisLivraison);
                        $('#total-commande').text((totalPanier + fraisLivraison).toFixed(2));
                    },
                    error: function (xhr, status, error) {
                        console.error('Erreur lors du calcul des frais de livraison :', error);
                        alert("Erreur lors du calcul des frais de livraison. Veuillez réessayer.");
                    }
                });
            }
        }
    }

    // Déclencher le calcul des frais de livraison lors de la modification des champs d'adresse ou de la méthode de commande
    $('#rue, #numero, #ville, #codePostal, #pays, #methodCommande').on('change', function () {
        calculateDeliveryFees();
    });

    // Assurer le calcul au chargement de la page si les champs sont remplis
    calculateDeliveryFees();

    function validateForm() {
        let isValid = true;
        const requiredFields = document.querySelectorAll('#orderInfoForm [required]');

        requiredFields.forEach(field => {
            const errorDiv = field.nextElementSibling;
            if (!field.value.trim()) {
                errorDiv.style.display = 'block';
                field.classList.add('is-invalid');
                isValid = false;
            } else {
                errorDiv.style.display = 'none';
                field.classList.remove('is-invalid');
            }
        });

        return isValid;
    }

    function toggleSubmitButton() {
        if (validateForm()) {
            $('#checkout-button').prop('disabled', false);
        } else {
            $('#checkout-button').prop('disabled', true);
        }
    }

    $('#orderInfoForm [required]').on('input', function () {
        toggleSubmitButton();
    });

    toggleSubmitButton();

    $('#checkout-button').on('click', function (event) {
        event.preventDefault();

        if (!validateForm()) {
            return;
        }

        const stripe = Stripe('pk_test_51LUs5LDjQcavZkZrmoldBAz0GwZXQK4EYEHJDMs3NQiqYZAMdaKG8z1MF8kJmgOO1sKNFW2ZqG30JtRTG9BLoHU300IugCOShr');

        var items = [];
        $('#cart-items tr').each(function () {
            var item = {
                name: $(this).find('td[name="nom_produit"]').text(),
                price: parseFloat($(this).find('#prix-produit').text()) * 100,
                quantity: parseInt($(this).find('.quantity-input').val())
            };
            items.push(item);
        });

        var orderInfo = {
            firstName: $('#prenom').val(),
            lastName: $('#nom').val(),
            email: $('#email').val(),
            rue: $('#rue').val(),
            numero: $('#numero').val(),
            localite: encodeURIComponent($('#localite').val()),
            ville: $('#ville').val(),
            codePostal: encodeURIComponent($('#codePostal').val()),
            departement: encodeURIComponent($('#departement').val()),
            pays: encodeURIComponent($('#pays').val()),
            orderMethod: $('#methodCommande').val(),
            idPanierStripe: $('#idPanierStripe').val(),
            montantCommande: $('#panier-total').text().replace(' €', ''),
            fraisLivraison: $('#frais-livraison').text().replace(' €', '') // Envoi des frais de livraison
        };

        console.log("Order Info: ", orderInfo);
        //alert("Order Info:\n" + JSON.stringify(orderInfo, null, 2));

        fetch('/api/checkout/create-session', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify({ items: items, orderInfo: orderInfo })
        })
        .then(function (response) {
            if (!response.ok) {
                return response.json().then(err => {throw new Error('Network response was not ok: ' + err.message)});
            }
            return response.json();
        })
        .then(function (data) {
            if (data.id) {
                return stripe.redirectToCheckout({ sessionId: data.id });
            } else {
                throw new Error('Session ID non trouvé dans la réponse.');
            }
        })
        .then(function (result) {
            if (result.error) {
                alert(result.error.message);
            }
        })
        .catch(function (error) {
            console.error('Error:', error);
            alert('Une erreur est survenue lors de la création de la session de paiement. Veuillez réessayer.');
        });

        // Prevent form submission for testing
        return false;
    });

    $(document).on('click', '.delete-first-line', function(event) {
        event.preventDefault();
        var form = $(this).closest('form');
        var panierId = form.find('input[name="idPanier"]').val();
        var ligneDeCommandeId = form.find('input[name="idLigneDeCommande"]').val();

        $.ajax({
            url: '/deleteFirstElemPanier',
            type: 'POST',
            data: {
                idPanier: panierId,
                idLigneDeCommande: ligneDeCommandeId
            },
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(response) {
                location.reload();
            },
            error: function(xhr, status, error) {
                alert("Erreur lors de la suppression de la ligne de commande : " + error);
            }
        });
    });

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
                    $("#cart-items").append('<tr><td colspan="4">Votre panier est vide.</td></tr>');
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
                $("#panier-total").text(totalPanier.toFixed(2) + ' €');
            },
            error: function (jqXHR, textStatus, errorThrown) {
                console.error("Erreur de requête Ajax: " + textStatus + ", " + errorThrown);
                alert("Une erreur est survenue lors de la récupération des lignes de commande du panier.");
            }
        });
    }

    function addToCart(produitId) {
        var produitIdValue = parseInt(produitId);
        if (isNaN(produitIdValue)) {
            console.error('Erreur : produitId invalide');
            return;
        }

        var quantiteInput = $("#quantite-" + produitIdValue);
        var quantite = parseInt(quantiteInput.val());
        if (quantite <= 0) {
            console.error('Quantité invalide');
            return;
        }

        var panierId = $("#panierId").val();
        var panierIdValue = parseInt(panierId);

        var data = {
            produitId: produitIdValue,
            panierId: panierIdValue,
            quantite: quantite
        };

        $.ajax({
            url: '/addToCart',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function (response) {
                console.log('Ligne de commande ajoutée avec succès :', response);
                getListLigneDeCommandePanier(panierIdValue);

                if ($("#cart-items").children().length === 0) {
                    location.reload();
                } else {
                    getListLigneDeCommandePanier(panierIdValue);
                }
            },
            error: function (error) {
                console.log(produitIdValue, panierIdValue, quantite, data);
                var errorMessage = error.responseJSON ? error.responseJSON.message : error.responseText;
                console.error('Erreur lors de l\'ajout de la ligne de commande :', errorMessage);
                alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
            }
        });

        quantiteInput.val(1);
    }

    function getPanier(idPanier, callback) {
        var idPanierval = parseInt(idPanier);

        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            url: '/panier/api/' + idPanierval,
            type: 'GET',
            dataType: 'json',
            headers: headers,
            success: function (panierData) {
                console.log('Panier récupéré avec succès :', panierData);
                if (callback) callback(panierData);
            },
            error: function (xhr, status, error) {
                console.error('Erreur AJAX :', error);
            }
        });
    }

    $(document).on('click', '.delete-line', function(event) {
        event.preventDefault();
        var form = $(this).closest('form');
        var ligneDeCommandeId = form.closest('tr').data('ligne-commande-id');

        $.ajax({
            url: '/lignedecommande/delete/' + ligneDeCommandeId,
            type: 'POST',
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(response) {
                location.reload();
            },
            error: function(xhr, status, error) {
                alert("Erreur lors de la suppression de la ligne de commande : " + error);
            }
        });
    });

    // Fonction globale, définie en dehors de tout bloc d'initialisation
    function updateLigneDeCommande(inputElement) {
        var ligneDeCommandeId = $(inputElement).data('ligne-commande-id');  // Récupérer l'id de la ligne de commande
        var ligneDeCommandePanierId = $(inputElement).closest('tr').find('td[name="ligneDeCommandePanierId"]').text(); // Récupérer l'id du panier
        var nouvelleQuantite = parseInt($(inputElement).val());  // Récupérer la valeur saisie dans le champ

        alert(ligneDeCommandeId, nouvelleQuantite);

        // Appel AJAX pour mettre à jour la quantité de la ligne de commande
        $.ajax({
            url: '/lignedecommande/update2',
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                id: ligneDeCommandeId,
                quantite: nouvelleQuantite
            }),
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);  // Utilisez les tokens CSRF
            },
            success: function(response) {
                console.log("Ligne de commande mise à jour avec succès:", response);
                // Mettre à jour le total de la ligne et le total du panier
                $("#total-ligne-" + ligneDeCommandeId).text(response.montantTotal + ' €');
                updateCartTotal(ligneDeCommandePanierId);  // Utilisation correcte de la variable ligneDeCommandePanierId
            },
            error: function(xhr, status, error) {
                let errorMessage = xhr.responseText || "Erreur inconnue lors de la mise à jour de la ligne de commande.";
                $("#error_" + ligneDeCommandeId).show().text(errorMessage);
                $("#confirmerPanier").prop("disabled", true);  // Désactiver le bouton de confirmation en cas d'erreur
                console.error("Erreur lors de la mise à jour de la ligne de commande:", errorMessage);
            }
        });
    }

    function updateCartTotal(idPanier) {
        var idPanierval = parseInt(idPanier);
        getPanier(idPanierval, function(panier) {
            var total = panier.montantTotalPanier;
            $('#panier-total').text(total.toFixed(2));
        });
    }


    function initialiserQuantitesMax() {
        $('.quantity-input').each(function() {
            var produitId = $(this).data('product-id');
            getQuantiteProduit(produitId, $(this));
        });
    }

    function getQuantiteProduit(produitId, inputElement) {
        $.ajax({
            type: "GET",
            url: "/quantite-produit/" + produitId,
            beforeSend: function(xhr) {
                xhr.setRequestHeader(csrfHeader, csrfToken);
            },
            success: function(quantite) {
                inputElement.attr('max', quantite);
            },
            error: function(error) {
                console.error('Erreur lors de la récupération de la quantité du produit:', error);
            }
        });
    }

    initialiserQuantitesMax();

    function getProduitDetails(produitId) {
        var produitIdValue = parseInt(produitId);

        var csrfParameterName = $("meta[name='_csrf.parameterName']").attr("content");
        var csrfToken = $("meta[name='_csrf.token']").attr("content");

        var headers = {};
        headers[csrfParameterName] = csrfToken;

        $.ajax({
            type: "GET",
            url: "/produit/api/" + produitIdValue,
            dataType: "json",
            headers: headers,
            success: function (data) {
                var nouvelleQuantite = data.quantite;

                $("#quantite-" + produitId).attr('max', nouvelleQuantite);
            },
            error: function (error) {
                console.error('Erreur lors de la récupération des détails du produit :', error);
                alert("Erreur lors de l'ajout du produit au panier. Veuillez réessayer.");
            }
        });
    }
});
