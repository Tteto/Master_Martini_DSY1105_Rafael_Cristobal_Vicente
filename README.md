# Martini Academy – DSY1105 Evaluación Parcial 2

Prototipo móvil (MVP académico) para **Master Martini Chile SPA**: plataforma de recetas, videos técnicos y cursos para clientes y prospectos del rubro gastronómico.

**Integrantes:** Vicente Verdugo · Rafael Miotta · Cristobal Cifuentes

**Repositorio:** https://github.com/Tteto/Master_Martini_DSY1105_Rafael_Cristobal_Vicente

## Funcionalidades
- Registro e inicio de sesión con **formularios validados** (ícono por campo, ícono de estado y mensaje animado): nombre, correo, teléfono chileno, RUT (módulo 11) y contraseña.
- **Encuesta de intereses** (tipo y tamaño de negocio, contenidos de interés) para segmentar usuarios.
- Catálogo de recetas, videos y cursos (datos **ficticios**) con búsqueda, filtros y detalle.
- **Favoritos** guardados por usuario.
- Interfaz accesible: tipografía grande, botones de 56 dp, íconos con descripción y alto contraste.
- **Animaciones:** transiciones entre pantallas, errores que aparecen/desaparecen, corazón con rebote, botones con efecto de pulsado, tarjeta de éxito y despliegue de pasos.
- **Recursos nativos:** 📷 cámara (foto de perfil, con permiso en tiempo de ejecución) y 📳 vibración (al guardar, marcar favorito o ante errores).
- **Persistencia local** con Room (usuarios y favoritos) + SharedPreferences (sesión).

## Arquitectura (MVVM)
```
data/        Entidades, DAOs, base Room, repositorio, contenido de ejemplo
util/        Validators (lógica de validación) y Haptics (vibración)
viewmodel/   AuthViewModel, ContentViewModel, estados de formulario
ui/          theme, components, screens (solo dibujan estado, sin lógica)
```

## Cómo ejecutar
1. Abrir la carpeta en **Android Studio** (Koala o superior, JDK 17) y esperar el *Gradle Sync*.
2. Elegir un emulador o dispositivo (API 24+) y presionar ▶ Run.
3. Crear cuenta con datos ficticios, por ejemplo: RUT `76123456-0`, teléfono `912345678`, contraseña `clave123`.
4. En el emulador, la cámara usa la cámara virtual; la vibración puede no sentirse.

> Todos los datos son ficticios. Colores y logo son de referencia hasta recibir el material autorizado por el socio formador.
