package com.studyflow.app.data.model

enum class LearningStatus(val label: String) {
    NOT_STARTED("Não iniciado"),
    STUDYING("Estudando"),
    REVIEW("Precisa revisar"),
    MASTERED("Dominado")
}

enum class TipType(val label: String) {
    CONCEPT("Conceito fundamental"),
    EXAM_TRAP("Pegadinha de prova"),
    OBSERVATION("Observação")
}

enum class MaterialType(val label: String) {
    LINK("Link"),
    REFERENCE("Referência"),
    PDF("PDF"),
    OTHER("Outro")
}

enum class Difficulty(val label: String) {
    EASY("Fácil"),
    MEDIUM("Médio"),
    HARD("Difícil")
}

enum class ErrorReason(val label: String) {
    UNKNOWN_CONTENT("Desconhecimento do conteúdo"),
    CONCEPT_CONFUSION("Confusão entre conceitos"),
    MISINTERPRETATION("Interpretação incorreta"),
    INATTENTION("Desatenção"),
    DOUBT_BETWEEN_OPTIONS("Dúvida entre alternativas"),
    OTHER("Outros")
}
