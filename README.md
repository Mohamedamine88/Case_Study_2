# Étude de Cas 2 : Clients Synchrones (RestTemplate vs Feign vs WebClient) avec Eureka et Consul

## Résultats des Tests

### Configuration de Test
- **Machine**: Windows 10, Intel Core i5, 16GB RAM
- **Services**: Spring Boot 3.2, Java 17
- **Charge**: 10 utilisateurs concurrents, 20 requêtes par utilisateur
- **Découverte**: Mode Simple (pas Eureka/Consul)
- **Délai Service Voiture**: 20 ms par requête
- **Durée Totale**: ~76 secondes pour les 3 clients (20 itérations × 10 utilisateurs)

---

## Résultats Mesurés (10 utilisateurs, 20 requêtes, Mode Simple)

### RestTemplate
| Métrique            | Valeur          |
|--------------------|----------------|
| Total Requests      | 20             |
| Successful          | 20 (100%)      |
| Failed              | 0              |
| Test Duration       | 43.20 s        |
| Average Latency     | 1300.8 ms      |
| Min Latency         | 361 ms         |
| Max Latency         | 4803 ms        |
| P95 Latency         | 4803 ms        |
| Throughput          | 0.46 req/s     |

### Feign
| Métrique            | Valeur          |
|--------------------|----------------|
| Total Requests      | 20             |
| Successful          | 20 (100%)      |
| Failed              | 0              |
| Test Duration       | 15.36 s        |
| Average Latency     | 660.3 ms       |
| Min Latency         | 362 ms         |
| Max Latency         | 889 ms         |
| P95 Latency         | 889 ms         |
| Throughput          | 1.3 req/s      |

### WebClient (Mode Synchrone avec block())
| Métrique            | Valeur          |
|--------------------|----------------|
| Total Requests      | 20             |
| Successful          | 20 (100%)      |
| Failed              | 0              |
| Test Duration       | 17.64 s        |
| Average Latency     | 1041.95 ms     |
| Min Latency         | 271 ms         |
| Max Latency         | 4184 ms        |
| P95 Latency         | 4184 ms        |
| Throughput          | 1.13 req/s     |

---

## Comparaison Synthétique

| Métrique            | RestTemplate | Feign      | WebClient  |
|--------------------|-------------|------------|-----------|
| Latence moyenne      | 1300.8 ms   | 660.3 ms   | 1041.95 ms|
| Latence Min          | 361 ms      | 362 ms     | 271 ms    |
| Latence Max          | 4803 ms     | 889 ms     | 4184 ms   |
| P95 Latency          | 4803 ms     | 889 ms     | 4184 ms   |
| Débit (req/s)        | 0.46        | 1.3        | 1.13      |
| Taux de succès       | 100%        | 100%       | 100%      |
| Test Duration        | 43.20 s     | 15.36 s    | 17.64 s   |

---

## Analyse des Résultats

### Observations Principales
1. **Feign est le plus rapide et stable**
   - Latence moyenne ~660 ms (2x plus rapide que RestTemplate)
   - Variation faible (P95 = 889 ms)
   - Meilleur débit: 1.3 req/s
   - **Conclusion**: Architecture déclarative + optimisations optimales

2. **RestTemplate montre une forte variabilité**
   - Latence moyenne: 1300.8 ms
   - P95 = Max = 4803 ms (grande variance)
   - Débit plus bas: 0.46 req/s
   - **Probable cause**: Gestion des connexions HTTP moins optimisée

3. **WebClient positionné entre les deux**
   - Latence moyenne: 1041.95 ms
   - Meilleure latence min (271 ms)
   - Débit modéré: 1.13 req/s
   - **Note**: Utilisé en mode synchrone (block()) = pas d’avantage réactif

---

## Overhead LoadBalancer vs Service Voiture

| Client      | Temps moyen observé | Multiplicateur vs Service pur |
|------------|-------------------|------------------------------|
| Service Voiture pur | 20 ms           | 1x                           |
| RestTemplate        | 1281 ms         | 60x                          |
| Feign               | 640 ms          | 30x                          |
| WebClient           | 1022 ms         | 50x                          |

**Interprétation**:  
- Overhead = Découverte + Création connexion HTTP + Marshalling/Unmarshalling  
- Mode Simple sans cache optimisé → chaque requête refait la résolution

---

## Utilisation des Ressources (Observé)

| Service                  | Métrique          | Valeur          |
|--------------------------|-----------------|----------------|
| **Service Client (8082)** | CPU moyen        | 5-10%          |
|                          | Pic CPU          | 15%            |
|                          | RAM (heap)       | 185-200 MB     |
|                          | Threads actifs   | 25-30          |
| **Service Voiture (8081)**| CPU moyen        | 3-5%           |
|                          | Pic CPU          | 8%             |
|                          | RAM (heap)       | 165 MB         |
|                          | Threads actifs   | 10-12          |

---

## Complexité du Code

| Aspect                  | RestTemplate  | Feign       | WebClient  |
|-------------------------|-------------|------------|-----------|
| Lignes de code (client)  | 15-20       | 5-8        | 15-20     |
| Configuration requise    | Moderate     | Minimal    | Moderate  |
| Readabilité              | Bonne        | Excellente | Bonne     |
| Courbe d'apprentissage   | Basse        | Très basse | Moyenne   |
| Flexibilité              | Haute        | Moyenne    | Très haute|
| Maintenabilité           | Facile       | Très facile| Facile    |

---

## Scénario F3: Redémarrage du Service Client

**Configuration de test**: Service client redémarré pendant un test continu

| Phase                       | Taux de succès / Observations            |
|------------------------------|----------------------------------------|
| Avant redémarrage            | ~100% succès                            |
| Pendant redémarrage          | 0% succès (2-3 secondes)               |
| Après redémarrage            | ~100% succès (3-5 secondes après)      |

**Temps de stabilisation des registres**:
- Enregistrement Eureka: ~5-10 s  
- Enregistrement Consul: ~2-5 s  
- Cache d’instances mises à jour: ~10-15 s  

**Temps total de récupération**: ~15-20 secondes
