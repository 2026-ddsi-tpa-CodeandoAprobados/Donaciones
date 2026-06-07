```mermaid

graph LR

    Usuario["Usuario"]
    
    subgraph solucion["Solución"] 
    
        subgraph servicioDonaciones["Servicio de Donaciones"]
        end
        
        subgraph servicioDonadoresYEntidades["Servicio de Donadores y Entidades"]          
        end
        
        subgraph servicioLogistica["Servicio de Logística"]
        end
    end
    
    Cliente --> servicioDonaciones
    servicioDonaciones -.-> servicioDonadoresYEntidades
    servicioDonaciones -.-> servicioLogistica
    
```

