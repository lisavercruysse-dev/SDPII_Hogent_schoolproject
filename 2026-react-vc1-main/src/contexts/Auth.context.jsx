import {
    useState,
    useCallback,
    useMemo,
} from 'react';
import useSWRMutation from 'swr/mutation';
import * as api from '../api';
import useSWR from 'swr';
import { JWT_TOKEN_KEY, AuthContext } from './auth';

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(localStorage.getItem(JWT_TOKEN_KEY)); 
 
  const {
    data: user,
    loading: userLoading,
    error: userError,
  } = useSWR(token ? 'werknemers/me' : null, api.getById);

  const {
    trigger: doLogin,
    isMutating: loginLoading,
    error: loginError,
  } = useSWRMutation('sessions', api.post);

  const {
    isMutating: registerLoading,
    error: registerError,
    trigger: doRegister,
  } = useSWRMutation('werknemers', api.post);

  const setSession = useCallback(
    (token) => {
      setToken(token);
      localStorage.setItem(JWT_TOKEN_KEY, token);
    },
    [],
  );
  
  const login = useCallback(
    async (email, password) => {
      try {
        const { token } = await doLogin({
          email,
          password,
        });

        setSession(token);

        return true; 
      } catch (error) {
        console.error(error);
        return false;
      }
    },
    [doLogin, setSession],
  );

  const logout = useCallback(() => {
    setToken(null);

    localStorage.removeItem(JWT_TOKEN_KEY);
  }, []);

  const value = useMemo(
    () => ({
      token,
      user,
      error: loginError || userError||registerError,
      loading: loginLoading || userLoading||registerLoading,
      isAuthed: Boolean(token),
      ready: !userLoading,
      login,
      logout,
    }),
    [
      token,
      user,
      loginError,
      loginLoading,
      userError,
      userLoading,
      registerError,
      registerLoading,
      login,
      logout,
    ],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};