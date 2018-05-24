package es.uca.iw.Ucapartment.Apartamento;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.sass.internal.parser.ParseException;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinService;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.renderers.ImageRenderer;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import es.uca.iw.Ucapartment.Home;
import es.uca.iw.Ucapartment.Administracion.PerfilUsuarioView;
import es.uca.iw.Ucapartment.Estado.Estado;
import es.uca.iw.Ucapartment.Estado.EstadoService;
import es.uca.iw.Ucapartment.Estado.Valor;
import es.uca.iw.Ucapartment.Iva.Iva;
import es.uca.iw.Ucapartment.Iva.IvaRespository;
import es.uca.iw.Ucapartment.Precio.Precio;
import es.uca.iw.Ucapartment.Precio.PrecioRepository;
import es.uca.iw.Ucapartment.Precio.PrecioService;
import es.uca.iw.Ucapartment.Reserva.Reserva;
import es.uca.iw.Ucapartment.Reserva.ReservaService;
import es.uca.iw.Ucapartment.Usuario.MiPerfilView;
import es.uca.iw.Ucapartment.Usuario.PopupPago;
import es.uca.iw.Ucapartment.Usuario.Usuario;
import es.uca.iw.Ucapartment.Valoracion.Valoracion;
import es.uca.iw.Ucapartment.Valoracion.ValoracionRepository;
import es.uca.iw.Ucapartment.security.SecurityUtils;


@SpringView(name = ApartamentoView.VIEW_NAME)
public class ApartamentoView extends VerticalLayout implements View {
	public static final String VIEW_NAME = "apartamentoView";

	private Grid<Apartamento> grid;
	private TextField filter;
	
	@Autowired
	PrecioRepository repo;
	@Autowired
	IvaRespository repoIva;
	@Autowired
	ValoracionRepository repoValoracion;
	
	
	private final ApartamentoService service;
	private final ReservaService serviceReserva;
	private final EstadoService serviceEstado;
	private final PrecioService precioService;
	private Apartamento apartamento;
	private Date entrada;
	private Date salida;
	private PopupPago sub = new PopupPago();
	private Usuario user;
	private List<Valoracion> listValoracion = new ArrayList<>();
	

	@Autowired
	public ApartamentoView(ApartamentoService service, ReservaService serviceReserva, EstadoService serviceEstado, PrecioService precioService) {
		this.service = service;
		this.serviceReserva = serviceReserva;
		this.serviceEstado = serviceEstado;
		this.precioService = precioService;
		
	}	
	
	void init() {
		
		Usuario duenio = apartamento.getUsuario();
		Button botonPerfil = new Button("Ver perfil");
		HorizontalLayout camposApartamento = new HorizontalLayout();
		VerticalLayout layoutDerecho = new VerticalLayout();
		VerticalLayout layoutIzquierdo = new VerticalLayout();
		VerticalLayout layoutComentario = new VerticalLayout();
		Panel reserva = new Panel("Información de la Reserva");
		Panel panelApartamento = new Panel("Apartamento "+apartamento.getNombre());
		Panel panelFoto = new Panel("Foto");
		Panel panelDuenio = new Panel("Dueño del apartamento");
		Panel panelComentario = new Panel("Comentarios y valoraciones");
		Button botonReserva = new Button("Reservar");
		botonReserva.setVisible(false);
		if(SecurityUtils.isLoggedIn() && (SecurityUtils.LogedUser().getId() != duenio.getId())) {
			botonReserva.setVisible(true);
		}
		
		reserva.setWidth("600px");
		reserva.setHeight("250px");
		panelApartamento.setWidth("600px");
		panelApartamento.setHeight("570px");
		panelFoto.setWidth("320px");
		panelFoto.setHeight("350px");
		panelDuenio.setWidth("320px");
		panelComentario.setWidth("900px");
		if(SecurityUtils.isLoggedIn())
		{
			layoutIzquierdo.addComponent(reserva);
		}
		
		layoutIzquierdo.addComponent(panelApartamento);
		
		layoutDerecho.addComponent(panelFoto);
		layoutDerecho.addComponent(panelDuenio);
		
		layoutComentario.addComponent(panelComentario);
		

	    camposApartamento.addComponent(layoutIzquierdo);
	    camposApartamento.addComponent(layoutDerecho);
	    
	    
	    VerticalLayout datosReserva = new VerticalLayout();
	    VerticalLayout elementosApartamento = new VerticalLayout();
	    VerticalLayout datosDuenio = new VerticalLayout();
	    
		HorizontalLayout hlNombre = new HorizontalLayout();
		HorizontalLayout hlDesc = new HorizontalLayout();
		HorizontalLayout hlContacto = new HorizontalLayout();
		HorizontalLayout hlCiudad = new HorizontalLayout();
		HorizontalLayout hlCalle = new HorizontalLayout();
		HorizontalLayout hlNumero = new HorizontalLayout();
		HorizontalLayout hlCP = new HorizontalLayout();
		HorizontalLayout hlHabit = new HorizontalLayout();
		HorizontalLayout hlCamas = new HorizontalLayout();
		HorizontalLayout hlAcond = new HorizontalLayout();
		HorizontalLayout hlNomDuenio = new HorizontalLayout();
		HorizontalLayout hlApellDuenio = new HorizontalLayout();
		HorizontalLayout hlEmailDuenio = new HorizontalLayout();
		
		Label vNombre, vDesc, vContacto, vCiudad, vCalle, vNumero, vCp, vHabit, vCamas, vAcond,
			vNombreDuenio, vApellidosDuenio, vEmailDuenio;
		Label nombre = new Label("Nombre: ");
		Label desc = new Label("Descripción: ");
		Label contacto = new Label("Contacto: ");
		Label ciudad = new Label("Ciudad: ");
		Label calle = new Label("Calle: ");
		Label numero = new Label("Número: ");
		Label cp = new Label("CP:");
		Label habit = new Label("Número de habitaciones: ");
		Label camas = new Label("Número de camas: ");
		Label acond = new Label("¿Tiene aire acondicionado? ");
		Label nombreDuenio = new Label("Nombre: ");
		Label apellidosDuenio = new Label("Apellidos: ");
		Label emailDuenio = new Label("Correo electrónico: ");
		
		vNombre = new Label(apartamento.getNombre());
		vDesc = new Label(apartamento.getDescripcion());
		vContacto = new Label(apartamento.getContacto());
		vCiudad = new Label(apartamento.getCiudad());
		vCalle = new Label(apartamento.getCalle());
		vNumero = new Label(String.valueOf(apartamento.getNumero()));
		vCp = new Label(String.valueOf(apartamento.getCp()));
		vHabit = new Label(String.valueOf(apartamento.getHabitaciones()));
		vCamas = new Label(String.valueOf(apartamento.getCamas()));
		if(apartamento.isAc())
			vAcond = new Label("Sí");
		else
			vAcond = new Label("No");
		
		vNombreDuenio = new Label(duenio.getNombre());
		vApellidosDuenio = new Label(duenio.getApellidos());
		vEmailDuenio = new Label(duenio.getEmail());
		
		Image image = new Image("foto");
		image.setWidth(300, Unit.PIXELS);
		image.setHeight(300, Unit.PIXELS);
		
    	image.setVisible(true);
    	if(apartamento.getFoto1() != null)
    		image.setSource(new ExternalResource(apartamento.getFoto1()));
		
		hlNombre.addComponents(nombre, vNombre);
		hlDesc.addComponents(desc, vDesc);
		hlContacto.addComponents(contacto, vContacto);
		hlCiudad.addComponents(ciudad, vCiudad);
		hlCalle.addComponents(calle, vCalle);
		hlNumero.addComponents(numero, vNumero);
		hlCP.addComponents(cp, vCp);
		hlHabit.addComponents(habit, vHabit);
		hlCamas.addComponents(camas, vCamas);
		hlAcond.addComponents(acond, vAcond);
		
		hlNomDuenio.addComponents(nombreDuenio, vNombreDuenio);
		hlApellDuenio.addComponents(apellidosDuenio, vApellidosDuenio);
		hlEmailDuenio.addComponents(emailDuenio, vEmailDuenio);
		
		
		
			
		//Para el Panel Reserva
		if(SecurityUtils.isLoggedIn())
		{
			datosReserva.addComponent(new Label("Ustedes ha elegido el apartamento "+apartamento.getNombre() +
					" de "+ apartamento.getHabitaciones() + " habitaciones  y con "+apartamento.getCamas()+" camas"));
			datosReserva.addComponent(new Label("Entrada: "+ entrada));
			datosReserva.addComponent(new Label("Salida: "+salida));
			long diasTotales = entrada.getTime() - salida.getTime();
			diasTotales = TimeUnit.DAYS.convert(diasTotales, TimeUnit.MILLISECONDS) * -1;
			double precioTotalSinIva = precioService.TotalPrecio(apartamento, entrada, salida);

			Iva iva = repoIva.findByPais("es");

			

			double porcentaje = (double)iva.getPorcentaje()/100;
			porcentaje = porcentaje * precioTotalSinIva;
			double precioTotal = precioTotalSinIva + porcentaje;
			datosReserva.addComponent(new Label("Precio Total: "+precioTotal+"€ (Precios especiales + IVA)"));
			HorizontalLayout reservaHorizontal = new HorizontalLayout();
			Button modificar = new Button("Modificar");
			Button reservar = new Button("Reservar");
			Button cancelar = new Button("Cancelar");
			Button reservarConfirmada = new Button("Reservar");
			reservaHorizontal.addComponent(modificar);
			reservaHorizontal.addComponent(reservar);
			datosReserva.addComponent(reservaHorizontal);
			VerticalLayout popupLayout = new VerticalLayout();
			
			modificar.addClickListener(event->{
				getUI().getNavigator().navigateTo(Home.VIEW_NAME);
			});
			cancelar.addClickListener(event ->{
				sub.close();
			});
			
			reservarConfirmada.addClickListener(event ->{
				Date hoy = java.sql.Date.valueOf(LocalDate.now());
				Reserva r = new Reserva(hoy,entrada,salida,precioTotal,user,apartamento);
				serviceReserva.save(r);
				
				Estado e = new Estado(hoy,Valor.PENDIENTE,r);
				serviceEstado.save(e);
				Notification.show("Gracias por confiar en UCApartment.\nSu reserva se ha realizado correctamente.", Notification.Type.HUMANIZED_MESSAGE );
				sub.close();
				getUI().getNavigator().navigateTo(Home.VIEW_NAME);
			});
			
			reservar.addClickListener(event->{
				popupLayout.removeAllComponents();
				popupLayout.addComponent(new Label("¿Estás seguro que deseas reservar?"));
				HorizontalLayout horizontal = new HorizontalLayout();
				horizontal.addComponent(cancelar);
				horizontal.addComponent(reservarConfirmada);
				popupLayout.addComponent(horizontal);
				sub.setWidth("400px");
				sub.setHeight("300px");
				sub.setPosition(550, 200);
				sub.setContent(popupLayout);
				sub.center();
				UI.getCurrent().addWindow(sub);
			});
		}
		
		//Comentarios
		Grid<Valoracion> gridValoracion = new Grid<>();
		gridValoracion.setWidth("897px");
		listValoracion = repoValoracion.findByApartamentoValorado(apartamento);
		gridValoracion.setItems(listValoracion);
		gridValoracion.addColumn(usuario ->{
			
			return usuario.getUsuario().getUsername();
		}).setCaption("Usuario");
		gridValoracion.addColumn(Valoracion::getDescripcion).setCaption("Comentario");
		gridValoracion.addColumn(p ->new ExternalResource("/valoracion/"+String.valueOf(p.getGrado()+".png")),new ImageRenderer()).setCaption("Valoración");
		
		
		
		elementosApartamento.addComponent(hlNombre);
		elementosApartamento.addComponent(hlDesc);
		elementosApartamento.addComponent(hlContacto);
		elementosApartamento.addComponent(hlCiudad);
		elementosApartamento.addComponent(hlCalle);
		elementosApartamento.addComponent(hlNumero);
		elementosApartamento.addComponent(hlCP);
		elementosApartamento.addComponent(hlHabit);
		elementosApartamento.addComponent(hlCamas);
		elementosApartamento.addComponent(hlAcond);
		elementosApartamento.addComponent(botonReserva);
		
		botonPerfil.addClickListener(event -> {
			getUI().getNavigator().navigateTo(PerfilUsuarioView.VIEW_NAME + '/'+String.valueOf(duenio.getId()));
		});
		
		datosDuenio.addComponent(hlNomDuenio);
		datosDuenio.addComponent(hlApellDuenio);
		datosDuenio.addComponent(hlEmailDuenio);
		datosDuenio.addComponent(botonPerfil);
		
		
		panelFoto.setContent(image);
		reserva.setContent(datosReserva);
		panelApartamento.setContent(elementosApartamento);
		panelDuenio.setContent(datosDuenio);
		panelComentario.setContent(gridValoracion);
		
		addComponent(camposApartamento);
	    setComponentAlignment(camposApartamento, Alignment.TOP_CENTER);
	    addComponent(layoutComentario);
	    setComponentAlignment(layoutComentario, Alignment.TOP_CENTER);
		
	}	
	
	@Override
	public void enter(ViewChangeEvent event) {
		// Obtenemos el id del apartamento de la URI
		String args[] = event.getParameters().split("/");
	    String value1 = args[0];
	    String dateString = args[1];
	    
	    try {
           
            entrada = java.sql.Date.valueOf(dateString);
            salida = java.sql.Date.valueOf(args[2]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
	    
	    try {
	    	user = SecurityUtils.LogedUser();
	    }catch(Exception e) {}

	    long id_apart = Long.parseLong(value1); // Como es un String lo convertimos a Long
	    apartamento = service.findById(id_apart); // Obtenemos el apartamento en cuestión de la BD
	    init(); // Y llamamos al metodo init que genera la vista
	}

}
