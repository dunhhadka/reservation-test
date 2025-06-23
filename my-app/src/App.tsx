import { DndProvider } from 'react-dnd'
import { HTML5Backend } from 'react-dnd-html5-backend'
import './App.css'
import RoomLayoutEditor from './RoomLayoutEditor'
import LoginPage from './LoginPage'
import { Provider } from 'react-redux'
import store from './store'
import BookingSeatPage from './BookingSeatsPage'

function App() {
  const employeeId = localStorage.getItem('employeeId')

  return (
    <Provider store={store}>
      <DndProvider backend={HTML5Backend}>
        <div className="App">
          {!!employeeId ? (
            <>
              <RoomLayoutEditor />
              {/* <BookingSeatPage /> */}
            </>
          ) : (
            <LoginPage />
          )}
        </div>
      </DndProvider>
    </Provider>
  )
}

export default App
