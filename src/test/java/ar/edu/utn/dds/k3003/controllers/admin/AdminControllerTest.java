package ar.edu.utn.dds.k3003.controllers.admin;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ar.edu.utn.dds.k3003.Fachada;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AdminControllerTest {

    private Fachada fachada;
    private MockMvc mvc;

    @BeforeEach
    void preparar() {
        fachada = mock(Fachada.class);
        AdminController controller = new AdminController();
        ReflectionTestUtils.setField(controller, "fachada", fachada);
        mvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void vaciarLaBaseResponde204() throws Exception {
        mvc.perform(delete("/admin/db")).andExpect(status().isNoContent());

        verify(fachada).vaciarBaseDeDatos();
    }

    @Test
    void republicarProductosDevuelveRepublicadosYTotal() throws Exception {
        when(fachada.republicarProductos()).thenReturn(Map.of("republicados", 2, "total", 3));

        mvc.perform(post("/admin/republicar-productos").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.republicados").value(2))
                .andExpect(jsonPath("$.total").value(3));
    }
}
