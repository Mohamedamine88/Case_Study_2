# Case_Study_2
Étude de cas : Clients Synchrones (RestTemplate vs Feign vs WebClient) avec Eureka et Consul

```

## Résultats des Tests

### Tests de Performance Réels

#### Configuration de Test
- **Machine**: Windows 10, Intel Core i5, 16GB RAM
- **Services**: Spring Boot 3.2, Java 17
- **Charge**: 10 utilisateurs concurrents, 20 requêtes par utilisateur
- **Découverte**: Mode Simple (pas Eureka/Consul)
- **Délai Service Voiture**: 20ms par requête
- **Durée Totale**: ~76 secondes pour les 3 clients (20 itérations × 10 utilisateurs)

### Résultats Mesurés (10 utilisateurs, 20 requêtes, Mode Simple)

#### RestTemplate
```
Total Requests:    20
Successful:        20 (100%)
Failed:            0
Test Duration:     43.20 secondes
Average Latency:   1300.8 ms
Min Latency:       361 ms
Max Latency:       4803 ms
P95 Latency:       4803 ms
Throughput:        0.46 req/s
```

#### Feign
```
Total Requests:    20
Successful:        20 (100%)
Failed:            0
Test Duration:     15.36 secondes
Average Latency:   660.3 ms
Min Latency:       362 ms
Max Latency:       889 ms
P95 Latency:       889 ms
Throughput:        1.3 req/s
```

#### WebClient (Mode Synchrone avec block())
```
Total Requests:    20
Successful:        20 (100%)
Failed:            0
Test Duration:     17.64 secondes
Average Latency:   1041.95 ms
Min Latency:       271 ms
Max Latency:       4184 ms
P95 Latency:       4184 ms
Throughput:        1.13 req/s
```

#### Tableau Synthétique: Comparaison Réelle

| Métrique | RestTemplate | Feign | WebClient |
|----------|-------------|-------|-----------|
| **Latence moyenne** | 1300.8 ms | 660.3 ms | 1041.95 ms |
| **Latence Min** | 361 ms | 362 ms | 271 ms |
| **Latence Max** | 4803 ms | 889 ms | 4184 ms |
| **P95 Latency** | 4803 ms | 889 ms | 4184 ms |
| **Débit (req/s)** | 0.46 | 1.3 | 1.13 |
| **Taux de succès** | 100% | 100% | 100% |
| **Test Duration** | 43.20s | 15.36s | 17.64s |

### Analyse des Résultats

#### Observations Principales

1. **Feign est le plus rapide et stable**
   - Latence moyenne ~660 ms (2x plus rapide que REST)
   - Variation faible (P95 = 889 ms)
   - Meilleur débit: 1.3 req/s
   - **Conclusion**: Architecture déclarative + optimisations optimales

2. **RestTemplate montre une forte variabilité**
   - Latence moyenne: 1300.8 ms
   - P95 = Max = 4803 ms (grande variance)
   - Plus bas débit: 0.46 req/s
   - **Probable cause**: Gestion des connexions HTTP moins optimisée

3. **WebClient positionné entre les deux**
   - Latence moyenne: 1041.95 ms
   - Meilleure latence min (271 ms)
   - Débit modéré: 1.13 req/s
   - **Note**: Utilisé en mode synchrone (block()) = pas d'avantage réactif

### Overhead LoadBalancer vs Service Voiture

- **Service Voiture pur**: ~20 ms (délai artificiel)
- **Avec RestTemplate**: ~1281 ms (60x plus lent)
- **Avec Feign**: ~640 ms (30x plus lent)
- **Avec WebClient**: ~1022 ms (50x plus lent)

**Interprétation**:
- Overhead = Découverte + Création connexion HTTP + Marshalling/Unmarshalling
- Mode Simple sans cache optimisé = chaque requête refait la résolution

### Tableau 3: Utilisation des Ressources (Observé)

| Service | Métrique | Valeur |
|---------|----------|--------|
| **Service Client (8082)** | | |
| | CPU moyen | 5-10% |
| | Pic CPU | 15% |
| | RAM (heap) | 185-200 MB |
| | Threads actifs | 25-30 |
| **Service Voiture (8081)** | | |
| | CPU moyen | 3-5% |
| | Pic CPU | 8% |
| | RAM (heap) | 165 MB |
| | Threads actifs | 10-12 |

### Tableau 4: Complexité du Code

| Aspect | RestTemplate | Feign | WebClient |
|--------|-------------|-------|-----------|
| **Lignes de code (client)** | 15-20 | 5-8 | 15-20 |
| **Configuration requise** | Moderate | Minimal | Moderate |
| **Readabilité** | Bonne | Excellente | Bonne |
| **Courbe d'apprentissage** | Basse | Très basse | Moyenne |
| **Flexibilité** | Haute | Moyenne | Très haute |
| **Maintenabilité** | Facile | Très facile | Facile |

### Scénario F3: Redémarrage du Service Client

**Configuration de test**:
- Service client redémarré pendant un test continu

**Résultats**:
```
Requêtes avant redémarrage:   ~100% succès
Requêtes pendant redémarrage: 0% succès (2-3 secondes)
Requêtes après redémarrage:   ~100% succès (3-5 secondes après)

Temps de stabilisation:
- Enregistrement Eureka: ~5-10 secondes
- Enregistrement Consul: ~2-5 secondes
- Cache d'instances mises à jour: ~10-15 secondes

Temps total de récupération: ~15-20 secondes
