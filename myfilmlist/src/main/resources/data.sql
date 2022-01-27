CREATE TABLE IF NOT EXISTS Genre(id INT primary key auto_increment, nom VARCHAR(100));
INSERT INTO Genre(nom) VALUES ('science fiction');
INSERT INTO Genre(nom) VALUES ('fantasy');

CREATE TABLE IF NOT EXISTS Realisateur(id INT primary key auto_increment, nom VARCHAR(100), prenom VARCHAR(100), date_naissance TIMESTAMP, celebre BOOLEAN);
INSERT INTO Realisateur(nom, prenom, date_naissance, celebre) VALUES('Cameron', 'James', '1954-08-16', false);
INSERT INTO Realisateur(nom, prenom, date_naissance, celebre) VALUES('Jackson', 'Peter', '1961-10-31', true);

CREATE TABLE IF NOT EXISTS Film(id INT primary key auto_increment, titre VARCHAR(100), duree INT, realisateur_id INT, genre_id INT);
INSERT INTO Film(titre, duree, realisateur_id, genre_id) VALUES('avatar', 162, 1, 1);
INSERT INTO Film(titre, duree, realisateur_id, genre_id) VALUES('La communauté de l''anneau', 178, 2, 2);
INSERT INTO Film(titre, duree, realisateur_id, genre_id) VALUES('Les deux tours', 179, 2, 2);
INSERT INTO Film(titre, duree, realisateur_id, genre_id) VALUES('Le retour du roi', 201, 2, 2);
