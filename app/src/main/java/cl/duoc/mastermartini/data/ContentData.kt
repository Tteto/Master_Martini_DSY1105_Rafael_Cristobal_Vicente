package cl.duoc.mastermartini.data

/** Contenido ficticio de ejemplo (el caso exige datos sinteticos). */
data class Content(
    val id: Int,
    val title: String,
    val type: String,
    val duration: String,
    val emoji: String,
    val description: String,
    val steps: List<String>
)

object ContentData {
    val types = listOf("Todos", "Receta", "Video", "Curso")

    val items = listOf(
        Content(1, "Brownie de chocolate intenso", "Receta", "45 min", "🍫",
            "Receta simple de brownie húmedo con cobertura de chocolate semiamargo.",
            listOf("Derretir chocolate y mantequilla.", "Mezclar con huevos, azúcar y harina.", "Hornear 25 minutos a 170°C.")),
        Content(2, "Atemperado de chocolate", "Video", "12 min", "🎬",
            "Video técnico sobre atemperado para lograr brillo y quiebre perfectos.",
            listOf("Fundir a 45°C.", "Enfriar a 27°C.", "Recalentar a 31°C y moldear.")),
        Content(3, "Curso de chocolatería", "Curso", "6 clases", "🎓",
            "Curso de especialidad para emprendedores: bombones, barras y rellenos.",
            listOf("Clase 1: Materias primas.", "Clase 2: Atemperado.", "Clase 3: Bombones rellenos.")),
        Content(4, "Pastelería de vitrina", "Curso", "8 clases", "🍰",
            "Aprende a preparar tortas y postres de alta rotación para tu vitrina.",
            listOf("Clase 1: Bizcochos base.", "Clase 2: Cremas y rellenos.", "Clase 3: Montaje y decoración.")),
        Content(5, "Masa de hoja casera", "Receta", "90 min", "🥐",
            "Masa de hojaldre paso a paso, ideal para bollería y empanadas.",
            listOf("Preparar la masa base.", "Incorporar la mantequilla.", "Dar 3 dobleces simples y reposar.")),
        Content(6, "Helado cremoso base", "Video", "15 min", "🍨",
            "Video sobre una base de helado artesanal y su correcto balance.",
            listOf("Pasteurizar la mezcla.", "Madurar 6 horas.", "Mantecar y congelar.")),
        Content(7, "Galletas de mantequilla", "Receta", "30 min", "🍪",
            "Galletas simples y crujientes para vender por unidad o en pack.",
            listOf("Batir mantequilla y azúcar.", "Agregar harina y vainilla.", "Hornear 12 minutos a 180°C.")),
        Content(8, "Empanadas al horno", "Video", "20 min", "🥟",
            "Técnica de armado y repulgue para empanadas uniformes.",
            listOf("Estirar la masa.", "Rellenar y cerrar.", "Pintar con huevo y hornear."))
    )
}
