# Diagrama de Clases - Dominio Interno

```mermaid
classDiagram
    class Fachada {
        -donacionesRepository: DonacionesRepository
        -historialEstadosRepository: HistorialEstadosDonacionRepository
        -donacionesDataMapper: DonacionesDataMapper
        -fachadaDonadoresYEnt: FachadaDonadoresYEntidades
        -fachadaLog: FachadaLogistica
        +registrarDonacion(DonacionDTO) DonacionDTO
        +buscarDonacionPorID(String) DonacionDTO
        +cambiarEstadoDeDonacion(String, EstadoDonacionEnum) DonacionDTO
        +buscarPorDonadorYFechaInicio(String, LocalDate) List~DonacionDTO~
        +registrarQuejaEnDonacion(String, String) DonacionDTO
        -donadorInhabilitado(String) void
        -donacionExistente(String) Donacion
    }

    class Donacion {
        <<Domain Object>>
        -id: String
        -donadorID: String
        -depositoID: String
        -productoID: String
        -cantidadProducto: Integer
        -estado: EstadoDonacionEnum
        -fechaRegistro: LocalDate
        +cumpleConFecha(LocalDate) Boolean
        +mismoDonador(String) Boolean
        +modificarEstado(EstadoDonacionEnum) void
    }

    class DonacionesRepository {
        <<Interface>>
        +save(Donacion) Donacion
        +findById(String) Optional~Donacion~
        +filterByDateAndDonadorID(String, LocalDate) List~Donacion~
    }

    class InMemoryDonacionesRepository {
        -donaciones: List~Donacion~
        -idSecuencial: AtomicLong
        +save(Donacion) Donacion
        +findById(String) Optional~Donacion~
    }

    class DonacionesDataMapper {
        +toDonacion(DonacionDTO) Donacion
        +toDonacionDTO(Donacion) DonacionDTO
        +donacionesToDonacionesDTO(List~Donacion~) List~DonacionDTO~
    }

    class HistorialEstadosDonacionRepository {
        <<Interface>>
        +save(String, EstadoDonacionEnum) void
    }

    %% Relaciones
    Fachada o-- DonacionesRepository : utiliza
    Fachada o-- HistorialEstadosDonacionRepository : utiliza
    Fachada o-- DonacionesDataMapper : utiliza
    
    DonacionesRepository <|.. InMemoryDonacionesRepository : implementa
    InMemoryDonacionesRepository "1" *-- "many" Donacion : contiene
    
    DonacionesDataMapper ..> Donacion : transforma
    Fachada ..> Donacion : gestiona