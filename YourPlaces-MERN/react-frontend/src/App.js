import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import { Users } from './user/pages/Users';
import { MainNavigation } from './shared/components/Navigation/MainNavigation';
import { NewPlace } from './places/pages/NewPlace';
import { UserPlaces } from './places/pages/UserPlaces';
import { UpdatePage } from './places/pages/UpdatePage';
import { Auth } from './user/pages/Auth';
import { AuthContext } from './shared/context/auth-context';
import { useAuth } from './shared/hooks/auth-hook';

// const NewPlace = React.lazy(() => import('./places/pages/NewPlace'))
// const UserPlaces = React.lazy(() => import('./places/pages/UserPlaces'))
// const UpdatePage = React.lazy(() => import('./places/pages/UpdatePage'))
// const Auth = React.lazy(() => import('./user/pages/Auth'))

function App() {
  const {token, login, logout, userId} = useAuth()
  let routes;
  if(token) {
    routes = (
      <>
        <Route path='/' element = {<Users />} exact='true' />
        <Route path='/:userId/places' element={<UserPlaces />} exact='true' />
        <Route path='/places/new' element = {<NewPlace />} exact='true' />
        <Route path='/places/:placeId' element={<UpdatePage />} exact='true' />
        <Route path='*' element = {<Users />} exact />
      </>
    )
  } else {
      routes = (
        <>
          <Route path='/' element = {<Users />} exact='true' />
          <Route path='/:userId/places' element={<UserPlaces />} exact='true' />
          <Route path='/login' element={<Auth />} exact='true' />
          <Route path='*' element = {<Auth />} exact='true' />
        </>
      )
  }

  return(
    <AuthContext.Provider value={{ isLoggedIn: !!token, token: token, userId: userId, login: login, logout: logout }} >
    <Router>
      <MainNavigation />
      <main>
      <Routes>
        {/* <Suspense fallback={<div className='center'><LoadingSpinner /></div>} > */}
          {routes}
        {/* </Suspense> */}
      </Routes>
      </main>
    </Router>
    </AuthContext.Provider>
  )
}

export default App;
