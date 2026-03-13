package cat.copernic.appvehicles.reserva.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cat.copernic.appvehicles.reserva.data.repository.ReservaRepository

/**
 * Patró de disseny Factory fabrica per a la instanciació del [ReservaViewModel].
 * * Aquesta classe és necessària per implementar la Injecció de Dependències (DI) manual.
 * Com que el ViewModel requereix paràmetres en el seu constructor (el repositori),
 * el framework d'Android no pot instanciar-lo per defecte. La Factory resol aquest
 * problema proveint la instància exacta amb les dependències requerides.
 *
 * @param repo El repositori de dades que s'injectarà al ViewModel.
 */
class ReservaViewModelFactory(
    private val repo: ReservaRepository
) : ViewModelProvider.Factory {

    /**
     * Mètode cridat pel cicle de vida d'Android per crear una nova instància del ViewModel.
     *
     * @param modelClass La classe del ViewModel que el sistema demana construir.
     * @return Una instància de [ReservaViewModel] amb el repositori ja injectat.
     * @throws IllegalArgumentException Si la classe sol·licitada no coincideix amb el ReservaViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReservaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReservaViewModel(repo) as T
        }
        throw IllegalArgumentException("Classe ViewModel desconeguda: s'esperava ReservaViewModel")
    }
}