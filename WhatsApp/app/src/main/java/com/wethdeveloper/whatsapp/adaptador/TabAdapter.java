package com.wethdeveloper.whatsapp.adaptador;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wethdeveloper.whatsapp.fragmentos.ContatosFragment;
import com.wethdeveloper.whatsapp.fragmentos.ConversasFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    /*array para guardar os titulos da abas*/
    private String[] abas = {"CONVERSAS","CONTATOS"};
    /*construtor padrão da classe*/
    public TabAdapter(FragmentManager fm) {
        super(fm);
    }
    /*método para retornar um fragment de acorda com a aba selecionada*/
    @Override
    public Fragment getItem(int i) {
        Fragment fragment = null;
        /*pega a posição*/
        switch (i){
            /*caso for a 0, é o de conversas*/
            case 0:
                fragment = new ConversasFragment();
                break;
            /*caso for 1, é o de contatos*/
            case 1:
                fragment = new ContatosFragment();
                break;
        }
        /*retorna no final o fragment instanciado*/
        return fragment;
    }

    @Override
    /*método para retornar a contagem de abas conforme o tamanho do array de titulos*/
    public int getCount() {
        return abas.length;
    }

    @Nullable
    @Override
    /*método padrão da classe para retornar o titulo da página de acordo com a posição*/
    public CharSequence getPageTitle(int position) {
        return abas[position];
    }
}
