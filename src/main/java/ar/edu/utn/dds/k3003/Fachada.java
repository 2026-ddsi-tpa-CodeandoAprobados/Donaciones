

package ar.edu.utn.dds.k3003;

import ar.edu.utn.dds.k3003.catedra.dtos.donaciones.*;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.DonadorDTO;
import ar.edu.utn.dds.k3003.catedra.dtos.donadoresYEntidades.QuejaDTO;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonaciones;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaDonadoresYEntidades;
import ar.edu.utn.dds.k3003.catedra.fachadas.FachadaLogistica;
import ar.edu.utn.dds.k3003.clients.DonadoresYEntidadesClient;
import ar.edu.utn.dds.k3003.clients.LogisticaClient;
import ar.edu.utn.dds.k3003.controllers.donaciones.DetalleProductoRequest;
import ar.edu.utn.dds.k3003.exceptions.categorias.*;
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
import ar.edu.utn.dds.k3003.model.categorias.Categoria;
import ar.edu.utn.dds.k3003.model.donaciones.Donacion;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import ar.edu.utn.dds.k3003.model.identificadores.Identificador;
import ar.edu.utn.dds.k3003.model.productos.DetalleProducto;
import ar.edu.utn.dds.k3003.model.productos.Producto;
import ar.edu.utn.dds.k3003.model.registroEstado.RegistroEstado;
import ar.edu.utn.dds.k3003.model.subcategorias.Subcategoria;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.categorias.CategoriasRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones.DonacionesDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.donaciones.DonacionesRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.historialEstados.HistorialEstadosDonacionRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores.IdentificadoresDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.identificadores.IdentificadoresRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.DetallesProductos.DetallesProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.productos.ProductosRepository;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasDataMapper;
import ar.edu.utn.dds.k3003.repositories_DataMapper.subcategorias.SubcategoriasRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class Fachada implements FachadaDonaciones {

    @Autowired
    @Getter @Setter private DonacionesRepository donacionesRepository;

    @Autowired
    @Getter @Setter private HistorialEstadosDonacionRepository historialEstadosRepository;

    @Autowired
    @Getter @Setter private ProductosRepository productosRepository;

    @Autowired
    @Getter @Setter private IdentificadoresRepository identificadoresRepository;

    @Autowired
    @Getter @Setter private CategoriasRepository categoriasRepository;

    @Autowired
    @Getter @Setter private SubcategoriasRepository subcategoriasRepository;

    @Autowired
    @Getter @Setter private DetallesProductosRepository detallesProductosRepository;

    @Autowired
    @Getter @Setter private DonacionesDataMapper donacionesDataMapper;

    @Autowired
    @Getter @Setter private ProductosDataMapper productosDataMapper;

    @Autowired
    @Getter @Setter private IdentificadoresDataMapper identificadoresDataMapper;

    @Autowired
    @Getter @Setter private CategoriasDataMapper categoriasDataMapper;

    @Autowired
    @Getter @Setter private SubcategoriasDataMapper subcategoriasDataMapper;

    @Autowired
    @Getter @Setter private DetallesProductosDataMapper detallesProductosDataMapper;

    private final DonadoresYEntidadesClient donadoresYEntidadesClient;

    private final LogisticaClient logisticaClient;

    public Fachada(DonadoresYEntidadesClient donadoresYEntidadesClient, LogisticaClient logisticaClient) {
        this.donadoresYEntidadesClient = donadoresYEntidadesClient;
        this.logisticaClient = logisticaClient;
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
        if (!(this.donadoresYEntidadesClient.verificarSiPuedeDonar(donadorID).get("puedeDonar"))) {
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
            throw new ProductoInexistente("El producto con ID " + productoID + " no existe");
        }
    }

    public void deleteDonacion(String donacionID) {

        val donacionOpcionalAeliminar = this.donacionesRepository.findById(Long.valueOf(donacionID));

        this.donacionNoEncontrada(donacionOpcionalAeliminar);

        val donacionAeliminar = donacionOpcionalAeliminar.get();

        this.donacionesRepository.delete(donacionAeliminar);

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

        this.logisticaClient.gestionarDonacion(donacionDTO);

        val donacionSinID = this.donacionesDataMapper.toDonacion(donacionDTO);

        donacionSinID.setDetallesProductos(detallesProductosGuardados);

        val donacionGuardada = this.donacionesRepository.save(donacionSinID);

        val nuevoRegistro =
                new RegistroEstado(String.valueOf(donacionGuardada.getId()) , EstadoDonacionEnum.INGRESADA);

        this.historialEstadosRepository.save(nuevoRegistro);

        return this.donacionesDataMapper.toDonacionDTO(donacionGuardada);
    }

    private void validarRegistroDetallesProductos(List<DetalleProductoDTO> detallesProductosDTOs) {

        this.validarDetallesProductos(detallesProductosDTOs);

        this.validarProductos(detallesProductosDTOs);

    }

    private void validarDetallesProductos(List<DetalleProductoDTO> detallesProductosDTOs) {

        if (detallesProductosDTOs.stream()
                .anyMatch(detalleProductoDTO -> detalleProductoDTO.id() != null)){
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

        val donadorDTO = this.donadoresYEntidadesClient.buscarDonadorPorID(donadorID);

        this.donadorNoEncontrado(donadorDTO);

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

        this.donacionesRepository.deleteById(donacionModificable.getId());

        val donacionModificada = (donacionModificable).modificarEstado(estado);

        val nuevoRegistro = new RegistroEstado(donacionID, estado);

        this.historialEstadosRepository.save(nuevoRegistro);

        val donacionGuardada = this.donacionesRepository.save(donacionModificada);

        return this.donacionesDataMapper.toDonacionDTO(donacionGuardada);
    }

    @Override
    public List<DonacionDTO> findByDonadorYFechaInicio(String donadorID, LocalDate fecha) {

        val donaciones = this.donacionesRepository.findAll();

        val donacionesCoincidentes = donaciones.
                stream().
                filter(d -> d.mismoDonador(donadorID) & d.cumpleConFecha(fecha)).
                toList();

        if (donacionesCoincidentes.isEmpty()) {
            throw new DonacionNoEncontrada("No hay ninguna donación que esté emparajada con el donador y fecha de inicio solicitados");
        }

        return this.donacionesDataMapper.donacionesToDonacionesDTO(donacionesCoincidentes);

    }

    public List<DonacionDTO> findDonacionesByDonadorID(String donadorID){

        val donaciones = this.donacionesRepository.findAll();

        val donacionesCoincidentes = donaciones.
                stream().
                filter(d -> d.mismoDonador(donadorID)).
                toList();

        if(donacionesCoincidentes.isEmpty()) {
            throw new DonacionNoEncontrada("No hay ninguana donación que esté emparajada con el donador brindado");
        }

        return this.donacionesDataMapper.donacionesToDonacionesDTO(donacionesCoincidentes);
    }

    @Override
    public DonacionDTO registrarQuejaEnDonacion(String donacionID, String descripcion) {

        val donacionRegistrada = donacionExistente(donacionID);

        val quejaGestionable
                = new QuejaDTO(null, donacionRegistrada.getId().toString(), donacionRegistrada.getDonadorID(), LocalDate.now(), descripcion);

        this.donadoresYEntidadesClient.agregarQueja(quejaGestionable);

        return cambiarEstadoDeDonacion(donacionID, EstadoDonacionEnum.CONQUEJA);

    }

    public void identificadorExiste(String identificadorID) {

        val identificadorOpcional = this.identificadoresRepository.findById(Long.valueOf(identificadorID));

        if(identificadorOpcional.isEmpty()) {
            throw new IdentificadorNoEncontrado("El identificador no ha sido encontrado");
        }
    }

    public Long cantidadDePalabras(String descripcionProducto){
        if (descripcionProducto.isBlank()) {
            return Long.valueOf(0);
        }

        return Stream.of(descripcionProducto.split(" "))
                .filter(palabra -> !palabra.isEmpty())
                .count();
    }

    public void productoValido(ProductoDTO productoDTO, Identificador identificador) {

        switch(identificador.getTipo()) {

            case QR :
                if(this.cantidadDePalabras(productoDTO.descripcion()) < 3){
                    throw new ProductoInvalido("El producto brindado es invalido");
                } break;

            case CODIGODEBARRAS :
                if (productoDTO.nombre().replace(" ","").length() % 2 != 0) {
                    throw new ProductoInvalido("El producto brindado es invalido");
                } break;
        }
    }

    public void eliminarCategoria(String categoriaID) {

        val categoriaAeliminar = this.categoriaExistente(categoriaID);

        this.categoriasRepository.delete(categoriaAeliminar);

    }

    public List<CategoriaDTO> findAllCategorias() {

        val categorias = this.categoriasRepository.findAll();

        if(categorias.isEmpty()) {
            throw new SinCategorias("No hay categorias cargadas en el sistema.");
        }
        return categorias.stream().map(categoria -> this.categoriasDataMapper.toCategoriaDTO(categoria)).toList();

    }

    public void categoriaExiste(String categoriaID){

        val categoriaOpcional = this.categoriasRepository.findById(Long.valueOf(categoriaID));

        if(categoriaOpcional.isEmpty()) {
            throw new CategoriaNoEncontrada("La categoria asociada a la subcategoria no existe");
        }
    }

    public Categoria categoriaExistente(String categoriaID) {

        val categoriaOpcional = this.categoriasRepository.findById(Long.valueOf(categoriaID));

        if(categoriaOpcional.isEmpty()) {
            throw new CategoriaNoEncontrada("La categoria no fue encontrada");
        }

        return categoriaOpcional.get();

    }

    public void subcategoriaExiste(String subcategoriaID) {

        val subcategoriaOpcional = this.subcategoriasRepository.findById(Long.valueOf(subcategoriaID));

        if(subcategoriaOpcional.isEmpty()) {
            throw new SubcategoriaDesconocida(  "La subcategoria no fue encontrada");
        }
    }

    public void subcategoriaDeProdExiste(ProductoDTO productoDTO) {

        if (productoDTO.subcategoriaID() == null ) {
            throw new SubcategoriaDesconocida("La subcategoría no puede ser desconocida");
        }

        val subcategoriaOpcional = this.subcategoriasRepository.findById(Long.valueOf(productoDTO.subcategoriaID().replace(" ","")));

        if (subcategoriaOpcional.isEmpty()) {
            throw new SubcategoriaNoEncontrada("La subcategoría asociada al producto no existe");
        }
    }

    public Subcategoria subcategoriaExistente(String subcategoriaID) {

        val subcategoriaOpcional = this.subcategoriasRepository.findById(Long.valueOf(subcategoriaID));

        if(subcategoriaOpcional.isEmpty()) {
            throw new SubcategoriaNoEncontrada("La subcategoria no fue encontrada");
        }

        return subcategoriaOpcional.get();

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

    public SubcategoriaDTO agregarSubcategoria(SubcategoriaDTO subcategoriaDTO) {

        this.categoriaExiste(subcategoriaDTO.categoriaID());

        val subcategoriaAagregar = this.subcategoriasDataMapper.toSubcategoria(subcategoriaDTO);

        val subcategoriaAgregada = this.subcategoriasRepository.save(subcategoriaAagregar);

        return this.subcategoriasDataMapper.toSubcategoriaDTO(subcategoriaAgregada);

    }

    public List<SubcategoriaDTO> findAllSubcategorias() {

        val subcategorias = this.subcategoriasRepository.findAll();

        if(subcategorias.isEmpty()) {
            throw new Sinsubcategorias("No hay subcategorías cargadas en el sistema.");
        }

        return subcategorias.stream().map(s -> this.subcategoriasDataMapper.toSubcategoriaDTO(s)).toList();

    }

    public List<DetalleProductoDTO> detallesFromRequestToDTOs(List<DetalleProductoRequest> detallesProductosRequest) {
        return this.detallesProductosDataMapper.fromRequestsToDTOs(detallesProductosRequest);

    }

    public SubcategoriaDTO findSubcategoriaById(String subcategoriaID) {

        val subcategoriaExistente = this.subcategoriaExistente(subcategoriaID);

        return this.subcategoriasDataMapper.toSubcategoriaDTO(subcategoriaExistente);

    }

    public void deleteSubcategoriaById(String subcategoriaID) {

        val subcategoriaExistente = this.subcategoriaExistente(subcategoriaID);

        this.subcategoriasRepository.deleteById(Long.valueOf(subcategoriaID));

    }

    public CategoriaDTO findCategoriaByProductoId(String productoID){

        val productoExistente = this.productoExistente(productoID);

        val subcategoriaDeProducto = this.subcategoriasRepository.findById(Long.valueOf(productoExistente.getSubcategoriaID())).get();

        val categoriaDeProducto = this.categoriasRepository.findById(Long.valueOf(subcategoriaDeProducto.getCategoriaID())).get();

        return this.categoriasDataMapper.toCategoriaDTO(categoriaDeProducto);

    }

    public void identificadorDeProdExiste(ProductoDTO productoDTO) {

        if(productoDTO.identificadorID() == null) {
            throw new IdentificadorDesconocido("El identificador no puede ser desconocido");
        }

        this.identificadorExiste(productoDTO.identificadorID());
    }

    public void validarProducto(ProductoDTO productoDTO) {

        if(productoDTO == null){
            throw new ProductoDesconocido("El producto brindado es invalido");
        }

        this.subcategoriaDeProdExiste(productoDTO);

        this.identificadorDeProdExiste(productoDTO);

        val identificador = this.identificadorExistente(productoDTO.identificadorID());

        this.productoValido(productoDTO, identificador);

    }

    @Override
    public ProductoDTO agregarProducto(ProductoDTO productoDTO) {

        this.validarProducto(productoDTO);

        val productoAguardar = this.productosDataMapper.toProducto(productoDTO);

        val productoGuardado = this.productosRepository.save(productoAguardar);

        return this.productosDataMapper.toProductoDTO(productoGuardado);
    }

    @Override
    public ProductoDTO buscarProductoPorID(String productoID) throws NoSuchElementException {

        val productoFinal = this.productoExistente(productoID);

        return this.productosDataMapper.toProductoDTO(productoFinal);

    }

    private void esInstanciaDeIdentificador(IdentificadorDTO identificadorDTO) {

        if(!(identificadorDTO.tipo().equals(TipoIdentificadorEnum.QR) || identificadorDTO.tipo().equals(TipoIdentificadorEnum.CODIGODEBARRAS) )) {
            throw new IdentificadorInvalido("El identificador no es instancia de uno ya creado en sistema");
        }

    }

    public Identificador identificadorExistente(String identificadorID) {

        val identificadorOpcional = this.identificadoresRepository.findById(Long.valueOf(identificadorID.replace(" ", "")));

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
        this.identificadoresRepository.deleteById(Long.valueOf(identificadorID));
    }

    public List<ProductoDTO> findAllProductos(){

        val productos = this.productosRepository.findAll();

        if(productos.isEmpty()) {
            throw new SinProductos("El sistema no tiene productos cargados");
        }

        return productos.stream().map(producto -> this.productosDataMapper.toProductoDTO(producto)).toList();

    }

    public ProductoDTO modificarProducto(String productoID, ProductoDTO productoDTOsinID){

        this.validarProducto(productoDTOsinID);

        this.eliminarProducto(productoID);

        val productoDTOconID = new ProductoDTO(
                productoID, productoDTOsinID.nombre(),
                productoDTOsinID.descripcion(),productoDTOsinID.subcategoriaID(),
                productoDTOsinID.identificadorID());

        val productoAguardar = this.productosDataMapper.toProducto(productoDTOconID);

        val productoGuardado = this.productosRepository.save(productoAguardar);

        return this.productosDataMapper.toProductoDTO(productoGuardado);
    }

    public void eliminarProducto(String productoID){

        val productoAeliminar =  this.productoExistente(productoID);
        this.productosRepository.delete(productoAeliminar);

    }

    @Override
    public IdentificadorDTO buscarIdentificadorPorID(String identificadorID) {

        val identificador = this.identificadorExistente(identificadorID);

        return this.identificadoresDataMapper.toIdentificadorDTO(identificador);

    }

    public List<IdentificadorDTO> findAllIdentificadores(){

        val identificadores = this.identificadoresRepository.findAll();

        if(identificadores.isEmpty()) {
            throw new SinIdentificadores("El sistema no tiene identificadores cargados");
        }

        return identificadores.stream().map(identificador -> this.identificadoresDataMapper.toIdentificadorDTO(identificador) ).toList();

    }

    public void vaciarBaseDeDatos(){
        this.identificadoresRepository.deleteAll();
        this.productosRepository.deleteAll();
        this.donacionesRepository.deleteAll();
        this.historialEstadosRepository.deleteAll();
        this.detallesProductosRepository.deleteAll();
        this.categoriasRepository.deleteAll();
        this.subcategoriasRepository.deleteAll();
    }

}
