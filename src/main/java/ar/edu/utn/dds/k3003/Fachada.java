

package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.exceptions.categorias.CategoriaDesconocida;
import ar.edu.utn.dds.k3003.exceptions.categorias.CategoriaNoEncontrada;
import ar.edu.utn.dds.k3003.exceptions.categorias.SinCategorias;
import ar.edu.utn.dds.k3003.exceptions.donaciones.*;
import ar.edu.utn.dds.k3003.exceptions.donadores.DonadorNoEncontrado;
import ar.edu.utn.dds.k3003.exceptions.identificadores.IdentificadorDesconocido;
import ar.edu.utn.dds.k3003.exceptions.identificadores.IdentificadorInvalido;
import ar.edu.utn.dds.k3003.exceptions.identificadores.IdentificadorNoEncontrado;
import ar.edu.utn.dds.k3003.exceptions.identificadores.SinIdentificadores;
import ar.edu.utn.dds.k3003.exceptions.productos.ProductoDesconocido;
import ar.edu.utn.dds.k3003.exceptions.productos.ProductoInexistente;
import ar.edu.utn.dds.k3003.exceptions.productos.ProductoInvalido;
import ar.edu.utn.dds.k3003.exceptions.productos.SinProductos;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import ar.edu.utn.dds.k3003.model.identificadores.Identificador;
import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.InMemoryCateRepo;
import ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones.DonacionesDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones.InMemoryDonacionesRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.historialEstados.HistorialEstadosDonacionRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.historialEstados.InMemoryHistorialDeEstadosRepo;
import ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores.IdentificadoresDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores.IdentificadoresRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores.InMemoryIdentificadorRepo;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.InMemoryDetalleProdRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.InMemoryProdRepo;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.InMemorySubcategoriaRepo;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.springframework.stereotype.Service;

@Service
public class Fachada implements FachadaDonaciones {

    @Getter @Setter private DonacionesRepository donacionesRepository;

    @Getter @Setter private HistorialEstadosDonacionRepository historialEstadosRepository;

    @Getter @Setter private ProductosRepository productosRepository;

    @Getter @Setter private IdentificadoresRepository identificadoresRepository;

    @Getter @Setter private CategoriasRepository categoriasRepository;

    @Getter @Setter private SubcategoriasRepository subcategoriasRepository;

    @Getter @Setter private DonacionesRepository donacionRepository;

    @Getter @Setter private DetallesProductosRepository detallesProductosRepository;

    @Getter @Setter private DonacionesDataMapper donacionesDataMapper = new DonacionesDataMapper();

    @Getter @Setter private ProductosDataMapper productoDataMapper = new ProductosDataMapper();

    @Getter @Setter private IdentificadoresDataMapper identificadoresDataMapper = new IdentificadoresDataMapper();

    @Getter @Setter private CategoriasDataMapper categoriasDataMapper = new CategoriasDataMapper();

    @Getter @Setter private SubcategoriasDataMapper subcategoriasDataMapper = new SubcategoriasDataMapper();

    @Getter @Setter private DetallesProductosDataMapper detallesProductosDataMapper = new DetallesProductosDataMapper();

    @Getter @Setter private FachadaDonadoresYEntidades fachadaDonadoresYEnt;

    @Getter @Setter private FachadaLogistica fachadaLog;

    public Fachada() {

        this.identificadoresRepository = new InMemoryIdentificadorRepo();
        this.donacionesRepository = new InMemoryDonacionesRepository();
        this.historialEstadosRepository = new InMemoryHistorialDeEstadosRepo();
        this.productosRepository = new InMemoryProdRepo();
        this.categoriasRepository = new InMemoryCateRepo();
        this.subcategoriasRepository = new InMemorySubcategoriaRepo();
        this.detallesProductosRepository = new InMemoryDetalleProdRepository();

    }

    public void cambioEstadoValido(Donacion donacion, EstadoDonacionEnum estado) {

        switch (estado) {

            case ACEPTADA:
                if (donacion.getEstado() != EstadoDonacionEnum.INGRESADA) {
                    throw new CambioEstadoInvalido("El cambio a ACEPTADA solo es válido desde INGRESADA.");
                }
                break;

            case CONQUEJA:
                if (donacion.getEstado() != EstadoDonacionEnum.ACEPTADA) {
                    throw new CambioEstadoInvalido("El cambio a CONQUEJA solo es válido desde ACEPTADA.");
                }
                break;

            default:
                throw new CambioEstadoInvalido("Transición de estado no permitida o estado no soportado.");

        }

    }

    public Donacion donacionValidadaParaCambioEstado(String donacionID, EstadoDonacionEnum estado) {

        this.estadoInvalido(estado);

        val donacion = this.donacionExistente(donacionID);

        this.cambioEstadoValido(donacion,estado);

        return donacion;

    }

    public void estadoInvalido(EstadoDonacionEnum estado) {

        if (estado == null) {
            throw new CambioEstadoInvalido("El estado recibido es inválido, no se le puede asignar el mismo a una donación.");
        }

    }

    public void donadorInhabilitado(String donadorID) {
        if (!(this.fachadaDonadoresYEnt.puedeDonar(donadorID))) {
            throw new DonacionNoSePuedeRegistrar("El donador está inhabilitado a realizar una donación.");
        }
    }

    public Donacion donacionExistente(String donacionID) {

        val donacionOpcional = this.donacionesRepository.findById(Long.valueOf(donacionID));

        this.donacionNoEncontrada(donacionOpcional);

        return donacionOpcional.get();
    }

    public void donacionDTOdesconocida(DonacionDTO donacionDTO) {
        if (donacionDTO == null) {
            throw new DonacionNoSePuedeRegistrar("La donación no puede ser desconocida.");
        }
    }

    public void donacionDTOconID(DonacionDTO donacionDTO) {
        if (donacionDTO.id() != null) {
            throw new DonacionNoSePuedeRegistrar("La donación ya existe, no se puede volver a registrar.");
        }
    }

    public void donadorNoEncontrado(DonadorDTO donadorDTO) {
        if (donadorDTO == null) {
            throw new DonadorNoEncontrado("El donador asociado a la donación no se encuentra registrado en el" +
                    "sistema");
        }
    }

    public void donacionNoEncontrada(Optional<Donacion> donacionOpcional) {
        if (donacionOpcional.isEmpty()) {
            throw new DonacionNoEncontrada("El ID brindado no está emparejado con una donación existente.");
        }
    }

    private void productoNoExiste(Optional<Producto> productoOpcional) {

        if (productoOpcional.isEmpty()) {
            throw new ProductoInexistente("El producto no existe");
        }
    }

    private Producto productoExistente(String productoID) {

        val productoOpcional = this.productosRepository.findById(Long.valueOf(productoID));

        this.productoNoExiste(productoOpcional);

        return productoOpcional.get();

    }

    private void productoExiste(String productoID){

        if(productoID == null) {
            throw new ProductoInvalido("El ID del producto es desconocida");
        }

        if(this.productosRepository.findById(Long.valueOf(productoID)).isEmpty()) {
            throw new ProductoInexistente(STR."El producto con ID \{productoID} no existe");
        }

    }

    public void eliminarDonacion(String donacionID) {

        val donacionAeliminar = this.donacionesRepository.findById(Long.valueOf(donacionID));

        this.donacionNoEncontrada(donacionAeliminar);

        this.donacionesRepository.deleteById(donacionID);

    }

    public List<DonacionDTO> findAllDonaciones(){

        val donaciones = this.donacionesRepository.findAll();

        if(donaciones.isEmpty()){
            throw new SinDonaciones("No hay donaciones cargadas en el sistema.");
        }

        return donaciones.stream().map(donacion -> this.donacionesDataMapper.toDonacionDTO(donacion)).toList();
    }

    public DonacionDTO registrarDonacion(DonacionDTO donacionDTO) {

        this.validarRegistroDonacion(donacionDTO);

        this.validarRegistroDetallesProductos(donacionDTO.detallesProductosDTO());

        val detallesProductosGuardados = this.registrarDetallesProductos(donacionDTO.detallesProductosDTO());

        val donacionSinID = this.donacionesDataMapper.toDonacion(donacionDTO);

        donacionSinID.setDetallesProductos(detallesProductosGuardados);

        val donacionGuardada = this.donacionesRepository.save(donacionSinID);

        this.fachadaLog.gestionarDonacion(
                donacionGuardada.getDepositoID(),
                String.valueOf(donacionGuardada.getId()),
                "donacionGuardada.getDetallesProductos()",
                123
        );

        this.historialEstadosRepository.save(String.valueOf(donacionGuardada.getId()), donacionGuardada.getEstado());

        return this.donacionesDataMapper.toDonacionDTO(donacionGuardada);
    }

    private void validarRegistroDetallesProductos(List<DetalleProductoDTO> detallesProductosDTOs) {

        this.validarDetallesProductos(detallesProductosDTOs);

        this.validarProductos(detallesProductosDTOs);

    }

    private void validarDetallesProductos(List<DetalleProductoDTO> detallesProductosDTOs) {

        if (detallesProductosDTOs.stream()
                .anyMatch(detalleProductoDTO -> detalleProductoDTO.id() != null || detalleProductoDTO == null)){
            throw new DonacionNoSePuedeRegistrar("Detalles de productos inválidos.");
        }
    }

    private void validarProductos(List<DetalleProductoDTO> detallesProductosDTOs) {
        detallesProductosDTOs.forEach(detalleProductoDTO -> this.productoExiste(detalleProductoDTO.productoID()));
    }

    private List<DetalleProducto> registrarDetallesProductos(List<DetalleProductoDTO> detallesProductosDTO){

        val detallesProductosAguardar = this.detallesProductosDataMapper.toDetallesProductos(detallesProductosDTO);

        return this.detallesProductosRepository.saveAll(detallesProductosAguardar);

    }

    private void validarRegistroDonacion(DonacionDTO donacionDTO) {

        this.donacionDTOdesconocida(donacionDTO);

        this.donacionDTOconID(donacionDTO);

        val donadorID = donacionDTO.donadorID();

        val donador = this.fachadaDonadoresYEnt.buscarDonadorPorID(donadorID);

        this.donadorNoEncontrado(donador);

        this.donadorInhabilitado(donadorID);
    }

    @Override
    public DonacionDTO buscarDonacionPorID(String donacionID) {

        val donacionFinal = this.donacionExistente(donacionID);

        return this.donacionesDataMapper.toDonacionDTO(donacionFinal);
    }

    @Override
    public DonacionDTO cambiarEstadoDeDonacion(String donacionID, EstadoDonacionEnum estado) {

        val donacionModificable = this.donacionValidadaParaCambioEstado(donacionID, estado);

        this.donacionesRepository.deleteById(String.valueOf(donacionModificable.getId()));

        val donacionModificada = (donacionModificable).modificarEstado(estado);

        this.historialEstadosRepository.save(donacionID, estado);

        val donacionGuardada = this.donacionesRepository.save(donacionModificada);

        return this.donacionesDataMapper.toDonacionDTO(donacionGuardada);
    }

    @Override
    public List<DonacionDTO> buscarPorDonadorYFechaInicio(String donadorID, LocalDate fecha) {

        val donacionesOpcionales = this.donacionesRepository.filterByDateAndDonadorID(donadorID, fecha);

        if (donacionesOpcionales.isEmpty()) {
            throw new DonacionNoEncontrada("No hay ninguna donación que esté emparajada con el donador y fecha de inicio solicitados");
        }

        return this.donacionesDataMapper.donacionesToDonacionesDTO(donacionesOpcionales);

    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {

        val donacionRegistrada = donacionExistente(donacionID);

        val quejaGestionable
                = new QuejaDTO(null, donacionRegistrada.getId().toString(), donacionRegistrada.getDonadorID(), LocalDate.now(), descripcion);

        this.fachadaDonadoresYEnt.agregarQueja(quejaGestionable);

        return cambiarEstadoDeDonacion(donacionID, EstadoDonacionEnum.CONQUEJA);

    }

    public void identificadorExiste(String identificadorID) {

        val identificadorOpcional = this.identificadoresRepository.findByID(Long.valueOf(identificadorID));

        if(identificadorOpcional.isEmpty()) {
            throw new IdentificadorNoEncontrado("El identificador no ha sido encontrado");
        }
    }

    public void productoValido(ProductoDTO productoDTO, Identificador identificador) {

        switch(identificador.getTipo()) {

            case QR :
                if(productoDTO.descripcion().length() < 3){
                    throw new ProductoInvalido("El producto brindado es invalido");
                } break;

            case CODIGODEBARRAS :
                if (productoDTO.nombre().length() % 2 != 0) {
                    throw new ProductoInvalido("El producto brindado es invalido");
                } break;
        }
    }

    public void eliminarCategoria(String categoriaID) {

        this.categoriaExiste(categoriaID);

        this.categoriasRepository.deleteById(categoriaID);

    }

    public List<CategoriaDTO> findAllCategorias() {

        val categorias = this.categoriasRepository.findAll();

        if(categorias.isEmpty()) {
            throw new SinCategorias("No hay categorias cargadas en el sistema.");
        }
        return categorias.stream().map(categoria -> this.categoriasDataMapper.toCategoriaDTO(categoria)).toList();

    }

    public void categoriaExiste(String categoriaID) {

        val categoriaOpcional = this.categoriasRepository.findById(Long.valueOf(categoriaID));

        if(categoriaOpcional.isEmpty()) {
            throw new CategoriaNoEncontrada("La categoria no fue encontrada");
        }

    }

    public void categoriaDesconocida(CategoriaDTO categoriaDTO) {

        if(categoriaDTO == null){
            throw new CategoriaDesconocida("La categoria no puede ser nula");
        }

    }

    public CategoriaDTO agregarCategoria(CategoriaDTO categoriaDTO) {

        this.categoriaDesconocida(categoriaDTO);

        val categoriaAagregar = this.categoriasDataMapper.toCategoria(categoriaDTO);

        val categoriaAgregada = this.categoriasRepository.save(categoriaAagregar);

        return this.categoriasDataMapper.toCategoriaDTO(categoriaAgregada);

    }

    public void categoriaDeProdExiste(ProductoDTO productoDTO) {

        if(productoDTO.categoriaID() != null) {
            this.categoriaExiste(productoDTO.categoriaID());
        }
    }

    public void identificadorDeProdExiste(ProductoDTO productoDTO) {

        if(productoDTO.identificadorID() == null) {
            throw new IdentificadorDesconocido("El identificador no puede ser desconocido");
        }

        val identificadorID = productoDTO.identificadorID();

        this.identificadorExiste(identificadorID);
    }

    public void validarProducto(ProductoDTO productoDTO) {

        if(productoDTO == null){
            throw new ProductoDesconocido("El producto brindado es invalido");
        }

        this.categoriaDeProdExiste(productoDTO);

        this.identificadorDeProdExiste(productoDTO);

        val identificador = this.identificadorExistente(productoDTO.identificadorID());

        this.productoValido(productoDTO, identificador);

    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {

        this.validarProducto(productoDTO);

        val productoAguardar = this.productoDataMapper.toProducto(productoDTO);

        val productoGuardado = this.productosRepository.save(productoAguardar);

        return this.productoDataMapper.toProductoDTO(productoGuardado);
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {

        val productoFinal = this.productoExistente(productoID);

        return this.productoDataMapper.toProductoDTO(productoFinal);

    }

    private void esInstanciaDeIdentificador(IdentificadorDTO identificadorDTO) {

        if(!(identificadorDTO.tipo().equals(TipoIdentificadorEnum.QR) || identificadorDTO.tipo().equals(TipoIdentificadorEnum.CODIGODEBARRAS) )) {
            throw new IdentificadorInvalido("El identificador no es instancia de uno ya creado en sistema");
        }

    }

    public Identificador identificadorExistente(String identificadorID) {

        val identificadorOpcional = this.identificadoresRepository.findByID(Long.valueOf(identificadorID));

        if(identificadorOpcional.isEmpty()) {
            throw new IdentificadorNoEncontrado("El identificador no ha sido encontrado");
        }

        return identificadorOpcional.get();

    }

    @Override
    public IdentificadorDTO agregarIdentificador(IdentificadorDTO identificadorDTO) {

        this.esInstanciaDeIdentificador(identificadorDTO);

        val identificacionAGuardar = this.identificadoresDataMapper.toIdentificador(identificadorDTO);

        val identificadorGuardado = this.identificadoresRepository.save(identificacionAGuardar);

        return this.identificadoresDataMapper.toIdentificadorDTO(identificadorGuardado);

    }

    public void eliminarIdentificador(String identificadorID) {

        this.identificadorExiste(identificadorID);
        this.identificadoresRepository.deleteById(identificadorID);
    }

    public List<ProductoDTO> findAllProductos(){

        val productos = this.productosRepository.findAll();

        if(productos.isEmpty()) {
            throw new SinProductos("El sistema no tiene productos cargados");
        }

        return productos.stream().map(producto -> this.productoDataMapper.toProductoDTO(producto)).toList();

    }

    public ProductoDTO modificarProducto(String productoID, ProductoDTO productoDTOsinID){

        this.validarProducto(productoDTOsinID);

        this.eliminarProducto(productoID);

        val productoDTOconID = new ProductoDTO(
                productoID, productoDTOsinID.nombre(),
                productoDTOsinID.descripcion(),productoDTOsinID.categoriaID(),
                productoDTOsinID.identificadorID());

        val productoAguardar = this.productoDataMapper.toProducto(productoDTOconID);

        val productoGuardado = this.productosRepository.save(productoAguardar);

        return this.productoDataMapper.toProductoDTO(productoGuardado);
    }

    public void eliminarProducto(String productoID){

        val productoAeliminar =  this.productoExistente(productoID);
        this.getProductosRepository().deleteById(String.valueOf(productoAeliminar.getId()));

    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) {

        val identificador = this.identificadorExistente(identificadorID);

        return this.identificadoresDataMapper.toIdentificadorDTO(identificador);

    }

    public List<IdentificadorDTO> findAllIdentificadores(){

        val identificadores = this.identificadoresRepository.findAllIdentificadores();

        if(identificadores.isEmpty()) {
            throw new SinIdentificadores("El sistema no tiene identificadores cargados");
        }

        return identificadores.stream().map(identificador -> this.identificadoresDataMapper.toIdentificadorDTO(identificador) ).toList();

    }

    @Override
    public void setFachadaDonadoresYEntidades(FachadaDonadoresYEntidades fachadaDonadoresYEntidades) {
        this.fachadaDonadoresYEnt = fachadaDonadoresYEntidades;
    }

    @Override
    public void setFachadaLogistica(FachadaLogistica fachadaLogistica) {
        this.fachadaLog = fachadaLogistica;
    }
}
